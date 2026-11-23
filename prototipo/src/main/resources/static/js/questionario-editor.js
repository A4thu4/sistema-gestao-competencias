/* Editor de questionário (UC-01): mostra as perguntas das competências marcadas, adiciona e remove perguntas,
   troca a escala e marca as competências padrão do cargo. Sem dependências. */
(function () {
    'use strict';
    var form = document.getElementById('form-questionario');
    if (!form) { return; }

    var proximo = parseInt(form.getAttribute('data-proximo-indice'), 10) || 0;
    var modelo = document.getElementById('modelo-pergunta');
    var caixas = form.querySelectorAll('input[name="competencias"]');

    function atualizarAvisoSemCompetencias() {
        var alguma = Array.prototype.some.call(caixas, function (cb) { return cb.checked; });
        var aviso = document.getElementById('sem-competencias');
        if (aviso) { aviso.classList.toggle('d-none', alguma); }
    }

    function adicionarPergunta(cid) {
        var html = modelo.innerHTML.split('XINDICEX').join(String(proximo++)).split('XCOMPX').join(cid);
        var lista = document.querySelector('#secao-' + cid + ' .lista-perguntas');
        lista.insertAdjacentHTML('beforeend', html);
        var campo = lista.lastElementChild.querySelector('textarea');
        if (campo) { campo.focus(); }
    }

    Array.prototype.forEach.call(caixas, function (cb) {
        cb.addEventListener('change', function () {
            var secao = document.getElementById('secao-' + cb.value);
            if (secao) {
                secao.classList.toggle('d-none', !cb.checked);
                if (cb.checked && !secao.querySelector('.pergunta-linha')) { adicionarPergunta(cb.value); }
            }
            atualizarAvisoSemCompetencias();
        });
    });

    form.addEventListener('click', function (e) {
        var botaoAdicionar = e.target.closest('.adicionar-pergunta');
        if (botaoAdicionar) { adicionarPergunta(botaoAdicionar.getAttribute('data-competencia')); return; }
        var botaoRemover = e.target.closest('.remover-pergunta');
        if (botaoRemover) { botaoRemover.closest('.pergunta-linha').remove(); }
    });

    // Escala de 1 a 5 ou de 1 a 10.
    var escala = document.getElementById('escalaMax');
    var sugestoes = {
        5: ['Nunca: não demonstra o comportamento, mesmo quando a situação pede.',
            'Raramente: demonstra o comportamento em poucas situações e costuma precisar de lembretes.',
            'Às vezes: demonstra o comportamento em parte das situações, ainda sem constância.',
            'Frequentemente: demonstra o comportamento na maior parte das situações.',
            'Sempre: demonstra o comportamento em todas as situações e serve de exemplo para os colegas.'],
        10: ['Nunca: não demonstra o comportamento.',
            'Quase nunca: demonstra o comportamento só em situações isoladas.',
            'Raramente: demonstra o comportamento em poucas situações.',
            'Pouco: demonstra o comportamento em menos da metade das situações.',
            'Às vezes: demonstra o comportamento em cerca de metade das situações.',
            'Com alguma frequência: demonstra o comportamento em mais da metade das situações.',
            'Frequentemente: demonstra o comportamento na maior parte das situações.',
            'Quase sempre: demonstra o comportamento com raras exceções.',
            'Sempre: demonstra o comportamento em todas as situações.',
            'Referência: demonstra o comportamento em todas as situações e ensina os colegas.']
    };
    function mostrarNiveis() {
        var max = parseInt(escala.value, 10);
        document.querySelectorAll('.nivel-linha').forEach(function (linha) {
            linha.classList.toggle('d-none', parseInt(linha.getAttribute('data-valor'), 10) > max);
        });
    }
    if (escala) {
        escala.addEventListener('change', mostrarNiveis);
        document.getElementById('sugerir-niveis').addEventListener('click', function () {
            var max = parseInt(escala.value, 10);
            document.querySelectorAll('.nivel-linha').forEach(function (linha) {
                var valor = parseInt(linha.getAttribute('data-valor'), 10);
                var campo = linha.querySelector('input');
                if (valor <= max && !campo.value.trim()) { campo.value = sugestoes[max][valor - 1]; }
            });
        });
    }

    // Competências padrão do cargo (RF-02).
    var padroes = window.padroesPorCargo || {};
    var cargo = document.getElementById('cargoId');
    function padraoDoCargo() {
        return (padroes[cargo.value] || []).map(String);
    }
    function mostrarSelosPadrao() {
        var lista = padraoDoCargo();
        document.querySelectorAll('[data-padrao-de]').forEach(function (selo) {
            selo.classList.toggle('d-none', lista.indexOf(selo.getAttribute('data-padrao-de')) < 0);
        });
    }
    if (cargo) {
        cargo.addEventListener('change', mostrarSelosPadrao);
        document.getElementById('marcar-padrao').addEventListener('click', function () {
            var lista = padraoDoCargo();
            Array.prototype.forEach.call(caixas, function (cb) {
                if (lista.indexOf(cb.value) >= 0 && !cb.checked) {
                    cb.checked = true;
                    cb.dispatchEvent(new Event('change'));
                }
            });
        });
    }
})();
