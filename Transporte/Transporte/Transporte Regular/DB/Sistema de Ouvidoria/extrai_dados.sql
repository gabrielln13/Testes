/*
---Script para extração de dados de empresas do Transporte Regular---

Objetivo: extrair os dados 'e-mail', 'nome fantasia', 'registro agr' dos bancos de dados Transporte e Cadastro Unico
para serem inseridos no sistema de ouvidoria.

Extração: os dados são exportados no formato de csv (comma separated values)

Instruções:
1. o arquivo só pode ser criado no servidor onde o BD está hospedado
2. deve criar uma pasta no servidor e dar permissão ao mysql
3. após isso mude o caminho (depois do comando 'into outfile') para a pasta criada
4. execute o código
5. navegue até o servidor para importar o seu arquivo para máquina local

6. o tipo de atividade '6' corresponde a 'Transporte Regular' no cadastro único

**esse processo de criação da pasta deve ser feito somente na primeira vez, se já houve uma importação dos dados,
deve somente executar o código e recuperar o arquivo na pasta
*/

select e.email, pj.nome_fantasia, crc.reg_agr
	from 	`transporte`.crc_cnpj_registroagr as crc,
			`cadastrounico`.pessoa_juridica as pj, 
			`cadastrounico`.email as e,
			`cadastrounico`.pessoa_juridica_atividades as pja
    where 	pj.cnpj = crc.cnpj 
			and e.pessoa_id = pj.id 
			and pja.pessoa_juridica_id = pj.id 
            and pja.atividade_id = 6 #tipo de atividade de Transporte Regular
	into outfile 'tmp//export_sql.csv' CHARACTER SET utf8
			fields terminated by ','
            escaped by '\\'
            lines terminated by '\r\n';