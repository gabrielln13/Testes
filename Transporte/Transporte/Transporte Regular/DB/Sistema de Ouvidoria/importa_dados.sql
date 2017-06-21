/*
---Script para importação de dados de empresas do Transporte Regular--

Objetivo: importar os dadoe de 'e-mail', 'nome fantasia', registro agr' para tabela tbunidade do banco de dados GOG (Sistema de Ouvidoria)

Importação: os dados serão importados do formato csv utilizando a codificação UTF8

Instruções:
1. Modifique o caminho de importação do arquivo
2. Execute o código

*/

copy tbunidade (eeemail, nmunidade, sgunidade) from 'D:/export_sql.csv' delimiter ',';
