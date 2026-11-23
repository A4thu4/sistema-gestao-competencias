/*
 * UC-03: salvamento automático de cada resposta (RF-14), progresso, descrição do nível escolhido (RF-25) e
 * conferência das obrigatórias antes do envio (RF-15). Sem JavaScript, os botões "Salvar rascunho" e "Enviar"
 * continuam funcionando pelo formulário.
 */
(function () {
    'use strict';
    const form = document.getElementById('formAvaliacao');
    if (!form) {
        return;
    }
    const urlSalvar = form.dataset.salvar;
    const csrf = form.querySelector('input[name="_csrf"]');
    const status = document.getElementById('statusSalvo');
    const perguntas = Array.from(form.querySelectorAll('.pergunta'));
    const temporizadores = {};

    function respondida(div) {
        if (div.dataset.tipo === 'ABERTA') {
            const t = div.querySelector('textarea');
            return t && t.value.trim() !== '';
        }
        return div.querySelector('input[type=radio]:checked') !== null;
    }

    function atualizarProgresso() {
        const total = perguntas.length;
        const feitas = perguntas.filter(respondida).length;
        const n = document.getElementById('respondidas');
        const barra = document.getElementById('barraProgresso');
        if (n) { n.textContent = feitas; }
        if (barra) { barra.style.width = (total ? Math.round(100 * feitas / total) : 0) + '%'; }
    }

    function mostrarDescricao(div) {
        const alvo = div.querySelector('.nivel-descricao');
        const marcado = div.querySelector('input[type=radio]:checked');
        if (alvo) { alvo.textContent = marcado ? marcado.dataset.descricao : ''; }
    }

    function salvar(div, dados) {
        const corpo = new URLSearchParams();
        corpo.append('perguntaId', div.dataset.pergunta);
        Object.keys(dados).forEach(function (k) { corpo.append(k, dados[k]); });
        if (csrf) { corpo.append(csrf.name, csrf.value); }
        status.classList.remove('erro');
        status.textContent = 'Salvando…';
        fetch(urlSalvar, { method: 'POST', body: corpo, headers: { 'Accept': 'application/json' } })
            .then(function (r) {
                return r.json().catch(function () { return {}; }).then(function (j) { return { ok: r.ok, j: j }; });
            })
            .then(function (res) {
                if (res.ok) {
                    status.textContent = 'Salvo às ' + res.j.salvoEm;
                } else {
                    status.classList.add('erro');
                    status.textContent = res.j.erro || 'Não foi possível salvar. Tente o botão "Salvar rascunho".';
                }
            })
            .catch(function () {
                status.classList.add('erro');
                status.textContent = 'Sem conexão. As últimas respostas não foram salvas.';
            });
    }

    perguntas.forEach(function (div) {
        mostrarDescricao(div);
        div.querySelectorAll('input[type=radio]').forEach(function (r) {
            r.addEventListener('change', function () {
                div.classList.remove('pendente');
                mostrarDescricao(div);
                atualizarProgresso();
                salvar(div, { nota: r.value });
            });
        });
        const texto = div.querySelector('textarea');
        if (texto) {
            texto.addEventListener('input', function () {
                atualizarProgresso();
                if (texto.value.trim() !== '') { div.classList.remove('pendente'); }
                clearTimeout(temporizadores[div.dataset.pergunta]);
                temporizadores[div.dataset.pergunta] = setTimeout(function () {
                    salvar(div, { texto: texto.value });
                }, 800);
            });
        }
    });

    const enviar = document.getElementById('botaoEnviar');
    const modal = document.getElementById('modalEnviar');
    if (enviar && modal && window.bootstrap) {
        enviar.addEventListener('click', function (ev) {
            ev.preventDefault();
            const pendentes = perguntas.filter(function (d) {
                return d.dataset.obrigatoria === 'true' && !respondida(d);
            });
            const caixa = document.getElementById('caixaPendentesJs');
            if (pendentes.length > 0) {
                pendentes.forEach(function (d) { d.classList.add('pendente'); });
                if (caixa) {
                    caixa.querySelector('span').textContent = pendentes.length === 1
                        ? 'Falta responder 1 pergunta obrigatória. Ela está marcada em vermelho.'
                        : 'Faltam responder ' + pendentes.length + ' perguntas obrigatórias. Elas estão marcadas em vermelho.';
                    caixa.classList.remove('d-none');
                }
                pendentes[0].scrollIntoView({ behavior: 'smooth', block: 'center' });
                const primeiro = pendentes[0].querySelector('input, textarea');
                if (primeiro) { primeiro.focus({ preventScroll: true }); }
                return;
            }
            if (caixa) { caixa.classList.add('d-none'); }
            bootstrap.Modal.getOrCreateInstance(modal).show();
        });
    }

    // Envio barrado no servidor (8a): leva ao primeiro item pendente.
    const primeiroPendente = form.querySelector('.pergunta.pendente');
    if (primeiroPendente && document.getElementById('caixaErro')) {
        primeiroPendente.scrollIntoView({ block: 'center' });
        const campo = primeiroPendente.querySelector('input, textarea');
        if (campo) { campo.focus({ preventScroll: true }); }
    }
    const confirmacao = document.getElementById('confirmacao');
    if (confirmacao) { confirmacao.scrollIntoView({ block: 'start' }); }
})();
