package br.edu.pucgoias.sgc.privacidade.servico;

/**
 * Texto da primeira versão do termo, em linguagem simples. Baseado no modelo do projeto
 * ({@code docs/anexos/Termo_de_Consentimento_Coleta_e_Tratamento_de_Dados.pdf}), adaptado ao uso do sistema.
 */
final class TextoTermo {

    private TextoTermo() {
    }

    static final String VERSAO_1 = """
            Para usar o Sistema de Gestão de Competências, precisamos que você concorde com o uso dos seus dados \
            pessoais. Leia com calma: aqui está, em poucas palavras, o que guardamos, para quê, quem vê e por \
            quanto tempo.

            ## 1. Quais dados usamos
            - Nome, e-mail, cargo, área e quem é o seu gestor direto.
            - As avaliações de competências em que você participa: as notas e os comentários que você recebe e \
            os que você dá.
            - As observações do seu gestor e o seu plano de desenvolvimento.
            - A data e a hora em que você aceitou este termo.
            Não pedimos documentos, endereço, dados de saúde nem nenhum outro dado que não seja necessário à \
            avaliação.

            ## 2. Para que usamos
            Só para avaliar competências, mostrar os resultados e montar o seu plano de desenvolvimento e o da \
            sua equipe. Os dados não são usados para outra finalidade sem um novo aceite seu.

            ## 3. Quem vê os seus dados
            - Você vê os seus próprios resultados.
            - O seu gestor direto vê os resultados da equipe dele.
            - O RH vê os resultados de todos para organizar os ciclos e os planos.
            As respostas de colegas e subordinados nunca mostram quem respondeu: aparecem só somadas, e só quando \
            há pelo menos três respostas. Ninguém, nem o RH, consegue ver a autoria. Os dados não são vendidos \
            nem repassados a outras empresas.

            ## 4. Por quanto tempo guardamos
            Enquanto você trabalhar na empresa. Depois disso, os dados são apagados ou ficam sem identificação, \
            menos o que a lei manda guardar (por exemplo, a prova deste aceite).

            ## 5. Os seus direitos
            - Ver uma cópia dos seus dados a qualquer momento.
            - Pedir a correção de um dado errado.
            - Pedir a exclusão dos seus dados.
            - Saber com quem os dados são compartilhados.
            Faça os pedidos na área "Meus dados" do sistema. Cada pedido recebe um número de protocolo e é \
            respondido pelo RH e pelo encarregado de dados.

            ## 6. Fale com o encarregado de dados
            Dúvidas ou reclamações sobre os seus dados: encarregado@exemplo.com.

            ## 7. Se você não aceitar
            Sem o aceite, não é possível usar o sistema, porque as avaliações dependem desses dados. Você pode \
            ler o termo de novo e aceitar quando quiser.
            """;
}
