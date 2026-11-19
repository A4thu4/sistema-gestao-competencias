#!/usr/bin/env python3
"""Gera o PDF único da Parte III (Documento de Arquitetura e Desenho Detalhado).

Junta os arquivos de secoes/ na ordem do sumário, insere os casos de uso marcados com
<!-- incluir: caminho -->, transforma links entre seções em links internos do PDF e os
demais links em endereços do GitHub, remove as dicas (> Dica: ...) e chama o pandoc.

Uso:  python docs/15-documento-arquitetura/gerar-pdf.py [--md]
      --md   grava também o Markdown combinado, para conferência

Requer pandoc e xelatex (no Windows, MiKTeX).
"""
from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

BASE = Path(__file__).resolve().parent
REPO = BASE.parents[1]
SECOES = [
    "00-capa-e-autoria.md",
    "01-introducao.md",
    "02-stakeholders.md",
    "03-requisitos-nao-funcionais.md",
    "04-ambiente-operacional-web.md",
    "05-1-nivel-contexto.md",
    "05-2-nivel-containers-cenarios.md",
    "05-3-nivel-componentes.md",
    "05-4-nivel-codigo.md",
    "06-referencias.md",
    "07-apendices.md",
]
SAIDA = REPO / "docs" / "impressao" / "parte-3-arquitetura.pdf"
GITHUB = "https://github.com/A4thu4/sistema-gestao-competencias"
TITULO = "Sistema de Gestão de Competências — Documento de Arquitetura e Desenho Detalhado"

INCLUIR = re.compile(r"<!--\s*incluir:\s*(\S+)\s*-->")
COMENTARIO = re.compile(r"<!--.*?-->", re.S)
TITULO_MD = re.compile(r"^(#{1,6})\s+(.*?)\s*$")
IMAGEM = re.compile(r"!\[([^\]]*)\]\(([^)\s]+)\)")
LINK = re.compile(r"(?<!!)\[((?:[^\[\]]|\[[^\]]*\])+)\]\(([^)\s]+)\)")


def ancora(texto: str) -> str:
    """Âncora no padrão do GitHub (igual a gfm_auto_identifiers do pandoc)."""
    texto = re.sub(r"[`*_]", "", texto).strip().lower()
    texto = re.sub(r"[^\w\- ]", "", texto)
    return texto.replace(" ", "-")


def blocos(texto: str):
    """Separa o texto em (é_código, trecho), para não mexer dentro de blocos de código."""
    partes = re.split(r"(^```.*?^```[^\n]*$)", texto, flags=re.S | re.M)
    for i, parte in enumerate(partes):
        yield i % 2 == 1, parte


def primeiro_titulo(arquivo: Path) -> str:
    for linha in arquivo.read_text(encoding="utf-8").splitlines():
        m = TITULO_MD.match(linha)
        if m:
            return ancora(m.group(2))
    return ""


def mapear_destinos() -> dict[Path, str]:
    """Arquivo do repositório -> âncora no PDF (seções e casos de uso incluídos)."""
    destinos = {}
    for nome in SECOES:
        arquivo = BASE / "secoes" / nome
        destinos[arquivo.resolve()] = primeiro_titulo(arquivo)
        ultimo = ""
        for linha in arquivo.read_text(encoding="utf-8").splitlines():
            m = TITULO_MD.match(linha)
            if m:
                ultimo = ancora(m.group(2))
            inc = INCLUIR.match(linha.strip())
            if inc:
                destinos[(arquivo.parent / inc.group(1)).resolve()] = ultimo
    destinos[(BASE / "documento-arquitetura.md").resolve()] = ""
    return destinos


class Montador:
    def __init__(self):
        self.destinos = mapear_destinos()
        self.faltando: list[str] = []
        self.dicas: list[str] = []

    def url_github(self, alvo: Path, frag: str) -> str:
        rel = alvo.relative_to(REPO).as_posix()
        tipo = "tree" if alvo.is_dir() else "blob"
        return f"{GITHUB}/{tipo}/main/{rel}" + (f"#{frag}" if frag else "")

    def link(self, pasta: Path, m: re.Match) -> str:
        texto, alvo = m.group(1), m.group(2)
        if re.match(r"^(https?:|mailto:|#)", alvo):
            return m.group(0)
        caminho, _, frag = alvo.partition("#")
        destino = (pasta / caminho).resolve()
        if destino in self.destinos:
            return f"[{texto}](#{frag or self.destinos[destino]})"
        try:
            return f"[{texto}]({self.url_github(destino, frag)})"
        except ValueError:
            return m.group(0)

    def imagem(self, pasta: Path, origem: str, m: re.Match) -> str:
        alt, alvo = m.group(1), m.group(2)
        if re.match(r"^https?:", alvo):
            return m.group(0)
        destino = (pasta / alvo).resolve()
        if destino.exists():
            return f"![{alt}]({destino.as_posix()})"
        rel = destino.relative_to(REPO).as_posix() if destino.is_relative_to(REPO) else alvo
        self.faltando.append(f"{origem}: {rel}")
        return f"**[Figura pendente: `{rel}`]**"

    def processar(self, arquivo: Path, nivel_extra: int = 0, descartar_h1: bool = False) -> str:
        origem = arquivo.relative_to(REPO).as_posix()
        pasta = arquivo.parent
        saida = []
        ultimo_nivel = 1
        for codigo, trecho in blocos(arquivo.read_text(encoding="utf-8")):
            if codigo:
                saida.append(trecho)
                continue
            linhas = []
            pular_dica = False
            for linha in trecho.split("\n"):
                if linha.startswith("> Dica"):
                    self.dicas.append(origem)
                    pular_dica = True
                    continue
                if pular_dica and linha.startswith(">"):
                    continue
                pular_dica = False
                inc = INCLUIR.match(linha.strip())
                if inc:
                    incluido = (pasta / inc.group(1)).resolve()
                    if incluido.exists():
                        linhas.append(self.processar(incluido, ultimo_nivel - 1, descartar_h1=True))
                    else:
                        self.faltando.append(f"{origem}: {incluido.relative_to(REPO).as_posix()} (caso de uso)")
                        linhas.append(f"**[Caso de uso pendente: `{inc.group(1)}`]**")
                    continue
                m = TITULO_MD.match(linha)
                if m:
                    nivel = len(m.group(1))
                    if descartar_h1 and nivel == 1:
                        continue
                    nivel += nivel_extra
                    ultimo_nivel = nivel
                    linha = "#" * min(nivel, 6) + " " + m.group(2) if nivel <= 6 else f"**{m.group(2)}**"
                linha = IMAGEM.sub(lambda mm: self.imagem(pasta, origem, mm), linha)
                linha = LINK.sub(lambda mm: self.link(pasta, mm), linha)
                linhas.append(linha)
            saida.append(COMENTARIO.sub("", "\n".join(linhas)))
        return "".join(saida)


def subir_titulos(texto: str) -> str:
    """O H1 do arquivo 00 vira o título do PDF; os demais títulos sobem um nível."""
    saida = []
    for codigo, trecho in blocos(texto):
        if not codigo:
            trecho = re.sub(r"^# .*\n?", "", trecho, count=1, flags=re.M) if not saida else trecho
            trecho = re.sub(r"^#(#{1,5}) ", r"\1 ", trecho, flags=re.M)
        saida.append(trecho)
    return "".join(saida)


def main() -> int:
    montador = Montador()
    partes = []
    for i, nome in enumerate(SECOES):
        partes.append(montador.processar(BASE / "secoes" / nome))
        if i == 0:
            partes.append("\n\n\\newpage\n\\setcounter{tocdepth}{3}\n\\tableofcontents\n\\newpage\n")
    corpo = subir_titulos("\n\n".join(partes))
    cabecalho = (
        "---\n"
        f'title: "{TITULO}"\n'
        "lang: pt-BR\n"
        "documentclass: article\n"
        "papersize: a4\n"
        "fontsize: 11pt\n"
        "geometry: margin=2.5cm\n"
        "mainfont: DejaVu Serif\n"
        "sansfont: DejaVu Sans\n"
        "monofont: DejaVu Sans Mono\n"
        "colorlinks: true\n"
        "block-headings: true\n"
        "---\n\n"
    )
    combinado = cabecalho + corpo

    if "--md" in sys.argv:
        (SAIDA.with_suffix(".md")).write_text(combinado, encoding="utf-8")

    if not shutil.which("pandoc"):
        print("pandoc não encontrado. Instale o pandoc e uma distribuição LaTeX com xelatex.")
        return 1
    SAIDA.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory() as tmp:
        fonte = Path(tmp) / "documento.md"
        fonte.write_text(combinado, encoding="utf-8")
        comando = [
            "pandoc", str(fonte), "-o", str(SAIDA),
            "-f", "markdown+gfm_auto_identifiers-implicit_figures-tex_math_dollars",
            "--pdf-engine=xelatex", "--resource-path", str(REPO),
        ]
        resultado = subprocess.run(comando, capture_output=True, text=True)
    if resultado.returncode != 0:
        print(resultado.stderr)
        return resultado.returncode

    print(f"PDF gerado: {SAIDA.relative_to(REPO).as_posix()}")
    if montador.faltando:
        print(f"\nFiguras ou casos de uso pendentes ({len(montador.faltando)}):")
        for item in montador.faltando:
            print(f"  - {item}")
    if montador.dicas:
        contagem = {a: montador.dicas.count(a) for a in dict.fromkeys(montador.dicas)}
        print("\nDicas ainda presentes no Markdown (foram omitidas no PDF):")
        for arquivo, n in contagem.items():
            print(f"  - {arquivo}: {n}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
