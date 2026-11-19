## 4. Descrição do ambiente operacional Web

> Dica: esta seção sustenta os critérios "Completude de processo" e "Corretude" da avaliação. Explique sempre com exemplos do **nosso** sistema (rotas, telas e casos de uso reais), não de forma genérica. Os nós descritos aqui são os mesmos do [Diagrama de Implantação](05-1-nivel-contexto.md#511-diagrama-de-implantação-dimp) (seção 5.1.1); combine os nomes com o Arthur.

### 4.1 Papel do browser, do servidor web e do servidor de aplicação

Autor: Gabriel Araújo Caetano · Revisor: Rikelmy Andrade · Issue: #61 · Referências: _RFC 9110 (semântica HTTP); documentação do Spring Boot/Tomcat; outras_

> Dica: o que cada software faz numa requisição do sistema. Sugestão de nós: **browser** (monta a requisição, guarda cookies, interpreta HTML, CSS e JavaScript); **servidor web** em proxy reverso, por exemplo Nginx (termina o TLS, serve arquivos estáticos, encaminha o resto); **servidor de aplicação** (Tomcat embutido no Spring Boot: contêiner de servlets, `DispatcherServlet`, filtros do Spring Security, controllers, Thymeleaf); **SGBD** PostgreSQL. Diga quais desses existem no protótipo local (sem proxy) e quais só na implantação (decisão `DA-07`).

**Tabela 4.1 — Softwares do ambiente operacional e suas responsabilidades**

| Software | Produto adotado | Responsabilidades na requisição |
|----------------|------------------------|------------------------------------------------------------|
| Browser | Chrome, Firefox, Edge, Safari (computador e celular) | _..._ |
| Servidor web | _Nginx_ | _..._ |
| Servidor de aplicação | Tomcat embutido no Spring Boot 3 | _..._ |
| Banco de dados | PostgreSQL 16 | _..._ |
| Serviços externos | Serviço de e-mail (SMTP), provedor de identidade (SSO) | _..._ |

Fonte: elaborado pelos autores (2026).

### 4.2 Protocolo HTTP

> Dica: estrutura da requisição (linha de requisição, cabeçalhos, linha em branco, corpo) e da resposta (linha de status, cabeçalhos, corpo). Métodos que o sistema usa e onde (GET para telas, POST para formulários, e o que muda com PUT/DELETE se a API REST para o aplicativo móvel vier, `RNF-07`). Cabeçalhos relevantes (`Host`, `Cookie`, `Set-Cookie`, `Content-Type`, `Location`, `Cache-Control`, `Strict-Transport-Security`). Códigos de status com o caso em que o sistema devolve cada um (200, 302 depois de um POST, 303, 400, 401/302 para o login, 403 para gestor fora da equipe, 404, 409, 500). Use um exemplo real, como o envio de uma avaliação (UC-03).

**Quadro 4.1 — Exemplo de requisição e resposta: envio de uma avaliação (UC-03)**

```http
POST /avaliacoes/42/envio HTTP/1.1
Host: _..._
Cookie: JSESSIONID=_..._
Content-Type: application/x-www-form-urlencoded

_csrf=_..._&resposta[101]=4&resposta[102]=5

HTTP/1.1 302 Found
Location: /avaliacoes?enviada=42
Set-Cookie: _..._
```

Fonte: elaborado pelos autores (2026).

**Tabela 4.2 — Métodos e códigos de status usados pelo sistema**

| Método ou código | Quando o sistema usa | Exemplo |
|----------------|------------------------------------------|------------------------------------------|
| `GET` | _..._ | _..._ |
| `POST` | _..._ | _..._ |
| `302` | _..._ | _..._ |
| `403` | _..._ | _..._ |
| _..._ | _..._ | _..._ |

Fonte: elaborado pelos autores (2026).

### 4.3 Ciclo completo de uma requisição

> Dica: siga uma requisição do sistema do começo ao fim, em etapas numeradas: (1) resolução do nome por DNS; (2) conexão TCP e negociação TLS 1.2+ (`RNF-04`); (3) envio da requisição; (4) processamento no servidor (proxy → Tomcat → filtros do Spring Security → `DispatcherServlet` → controller → serviço → repositório → PostgreSQL); (5) geração da resposta (Thymeleaf monta o HTML); (6) renderização no cliente (DOM, CSS, JavaScript, requisições adicionais de CSS, JS e imagens, gráficos com Chart.js na TL-04). Um diagrama de sequência desse ciclo ajuda muito; ele pode ser a Figura 4.1.

**Figura 4.1 — Ciclo completo de uma requisição no sistema**

![Ciclo completo de uma requisição](../diagramas/dseq-ciclo-requisicao.png)

Fonte: elaborado pelos autores (2026).

_Texto das etapas._

### 4.4 Mecanismos de sessão e de manutenção de estado

Autor: Lorenzo Oliveira Maciel · Revisor: Rikelmy Andrade · Issue: #67 · Referências: _RFC 6265 (cookies); documentação do Spring Security; OWASP Session Management Cheat Sheet; outras_

> Dica: o HTTP não guarda estado; explique como o sistema guarda. Sessão no servidor (`HttpSession`) identificada pelo cookie `JSESSIONID` com `HttpOnly`, `Secure` e `SameSite`; troca do identificador no login (fixação de sessão); tempo de expiração; token CSRF nos formulários; o que fica na sessão (usuário e perfil) e o que fica no banco (rascunho da avaliação, salvo automaticamente). Avaliador externo: sem sessão de usuário, o estado vem do token do link individual (`RF-34`). Login por SSO: redirecionamentos e o retorno ao sistema (`RF-32`). Pense também no aplicativo móvel futuro (token em vez de cookie).

**Tabela 4.3 — Onde o sistema guarda cada estado**

| Estado | Onde fica | Por quanto tempo | Requisito |
|------------------------------|-------------------------|-------------------------|--------------------|
| Usuário autenticado e perfil | _Sessão no servidor_ | _..._ | `RF-31`, `RF-33` |
| Rascunho da avaliação | _Banco de dados_ | _..._ | _..._ |
| Acesso do avaliador externo | _Token no link_ | _..._ | `RF-34` |
| _..._ | _..._ | _..._ | _..._ |

Fonte: elaborado pelos autores (2026).

### 4.5 Formas de invocação da aplicação e resultados de cada etapa

> Dica: as maneiras como o sistema é acionado e o que cada etapa produz. Formas sugeridas: digitar a URL ou usar um favorito; clicar em link ou menu (GET); enviar formulário (POST + redirecionamento); requisição assíncrona em JavaScript (salvamento automático do rascunho na TL-03); link recebido por e-mail (convite do ciclo, link do avaliador externo); redirecionamento do provedor de identidade (SSO); tarefa agendada no servidor, sem browser (lembretes por e-mail, `RF-12` e `RF-28`).

**Tabela 4.4 — Formas de invocação e resultados gerados**

| Forma de invocação | Exemplo no sistema | Requisição gerada | Processamento no servidor | Resultado devolvido | Resultado no cliente |
|--------------|------------------|--------------|--------------------|--------------|--------------------|
| Navegação por link | _Abrir "Meus resultados" (UC-04)_ | `GET /resultados` | _..._ | `200` + HTML | _Tela TL-04 renderizada_ |
| Envio de formulário | _..._ | _..._ | _..._ | _..._ | _..._ |
| Requisição assíncrona | _..._ | _..._ | _..._ | _..._ | _..._ |
| Link por e-mail | _..._ | _..._ | _..._ | _..._ | _..._ |
| Tarefa agendada | _Lembrete de avaliação pendente_ | _nenhuma (sem browser)_ | _..._ | _e-mail enviado_ | _..._ |

Fonte: elaborado pelos autores (2026).
