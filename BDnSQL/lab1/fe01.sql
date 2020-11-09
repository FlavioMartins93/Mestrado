CREATE TABLE autor (
	id_autor NUMBER(3,0) NOT NULL ENABLE, 
	nome VARCHAR2(200 BYTE) NOT NULL ENABLE, 
	CONSTRAINT pk_autor PRIMARY KEY (id_autor));

CREATE TABLE editora (
	id_editora NUMBER(3,0) NOT NULL ENABLE, 
	nome VARCHAR2(200 BYTE) NOT NULL ENABLE, 
	CONSTRAINT pk_editora PRIMARY KEY (id_editora));

CREATE TABLE genero (
	id_genero NUMBER(3,0) NOT NULL ENABLE, 
	nome VARCHAR2(200 BYTE) NOT NULL ENABLE, 
	CONSTRAINT pk_genero PRIMARY KEY (id_genero));
	
CREATE TABLE suporte (
	id_suporte NUMBER(3,0) NOT NULL ENABLE, 
	nome VARCHAR2(200 BYTE) NOT NULL ENABLE, 
	CONSTRAINT pk_support PRIMARY KEY (id_suporte));

CREATE TABLE titulo (
	id_titulo NUMBER(3,0) NOT NULL ENABLE, 
	titulo VARCHAR2(200 BYTE) NOT NULL ENABLE, 
	preco NUMBER NOT NULL ENABLE, 
	dta_compra DATE NOT NULL ENABLE, 
	id_editora NUMBER(3,0) NOT NULL ENABLE, 
	id_suporte NUMBER(3,0) NOT NULL ENABLE, 
	id_genero NUMBER(3,0) NOT NULL ENABLE, 
	id_autor NUMBER(3,0) NOT NULL ENABLE, 
	CONSTRAINT pk_titulo PRIMARY KEY (id_titulo),
	CONSTRAINT fk_t_id_editora FOREIGN KEY (id_editora)
		REFERENCES editora (id_editora) ENABLE, 
	CONSTRAINT fk_t_id_suporte FOREIGN KEY (id_suporte)
		REFERENCES suporte (id_suporte) ENABLE, 
	CONSTRAINT fk_t_id_genero FOREIGN KEY (id_genero)
		REFERENCES genero (id_genero) ENABLE, 
	CONSTRAINT fk_t_id_autor FOREIGN KEY (id_autor)
		REFERENCES autor (id_autor) ENABLE);

CREATE TABLE musica (
	id_musica NUMBER(3,0) NOT NULL ENABLE, 
	nome VARCHAR2(200 BYTE) NOT NULL ENABLE, 
	id_autor NUMBER(3,0) NOT NULL ENABLE, 
	id_titulo NUMBER(3,0) NOT NULL ENABLE, 
	CONSTRAINT pk_musica PRIMARY KEY (id_musica),
	CONSTRAINT fk_m_id_autor FOREIGN KEY (id_autor) 
		REFERENCES AUTOR (id_autor) ENABLE, 
	CONSTRAINT fk_m_id_titulo FOREIGN KEY (id_titulo) 
		REFERENCES titulo (ID_TITULO) ENABLE);

CREATE TABLE review (
	id_review NUMBER(3,0) NOT NULL ENABLE, 
	id_titulo NUMBER(3,0) NOT NULL ENABLE, 
	dta_review DATE NOT NULL ENABLE, 
	conteudo VARCHAR2(300 BYTE) NOT NULL ENABLE, 
	CONSTRAINT pk_review PRIMARY KEY (id_review),
	CONSTRAINT fk_id_titulo FOREIGN KEY (id_titulo)
		REFERENCES titulo (id_titulo) ENABLE);

-- [a] Quantos títulos possui a coleção?
SELECT COUNT(*) "Total de titulos" FROM titulo;

-- [b] Quantas músicas no total possui toda a coleção?
SELECT COUNT(*) "Total de musicas" FROM musica;

-- [c] Quantos autores existem na coleção?
SELECT COUNT(*) "Total de autores" FROM autor;

-- [d] Quantas editoras distintas existem na coleção?
SELECT COUNT(*) "Total de editoras" FROM editora;

-- [e] O autor “Max Changmin” é o principal autor de quantos título?
SELECT COUNT(*) "Total de titulos"
FROM titulo t
INNER JOIN autor a
	ON t.id_autor = a.id_autor
WHERE a.nome = 'Max Changmin';

-- [f] No ano de 1970, quais foram os títulos comprados pelo utilizador?
SELECT titulo FROM titulo
WHERE EXTRACT( YEAR FROM dta_compra) = 1970; 

-- [g] Qual o autor do título que foi adquirido em “01-02-2010”, cujo preço foi de 12€?
SELECT nome FROM titulo t
INNER JOIN autor a 
	ON t.id_autor = a.id_autor
WHERE dta_compra = date '2010-02-01' AND preco = 12;

-- [h] Na alínea anterior indique qual a editora desse título?
SELECT t.nome, e.nome FROM 
	(SELECT nome, id_editora
	FROM titulo t
	INNER JOIN autor a
		ON t.id_autor = a.id_autor
	WHERE dta_compra = date '2010-02-01' AND preco = 12) t
INNER JOIN editora e
	 ON t.id_editora = e.id_editora;

-- [i] Quais as reviews (data e classificação) existentes para o título “oh whoa oh” ?
SELECT r.dta_review Data, r.conteudo Classificação
FROM titulo t
INNER JOIN review r ON r.id_titulo = t.id_titulo
WHERE t.titulo = 'oh whoa oh';

-- [j] Quais as reviews (data e classificação) existentes para o título “pump”, 
-- ordenadas por data da mais antiga para a mais recente?
SELECT r.dta_review, r.conteudo 
FROM titulo t
INNER JOIN review r
	ON r.id_titulo = t.id_titulo
WHERE t.titulo = 'pump'
ORDER BY r.dta_review ASC;

-- [k] Quais os diversos autores das músicas do título lançado a ‘04-04-1970’ com o preço de 20€?
-- assumindo que qualquer compra feita após 04-04-1970 a 20€ pode ser o titulo em questão
SELECT a.nome 
FROM musica m
INNER JOIN titulo t
	ON m.id_titulo = t.id_titulo
INNER JOIN autor a
    ON m.id_autor = a.id_autor
WHERE t.dta_compra = date '1970-04-04' AND t.preco = 20;

-- [l] Qual foi o total de dinheiro investido em compras de título da editora ‘EMI’?
SELECT SUM(preco) "Total Investido"
FROM titulo t 
INNER JOIN editora e
	ON t.id_editora = e.id_editora
WHERE e.nome = 'EMI';

-- [m] Qual o título mais antigo cujo preço foi de 20€?
SELECT titulo
FROM titulo
WHERE preco = 20
ORDER BY dta_compra ASC
FETCH FIRST 1 ROWS ONLY;

-- [n] Quantos “MP3” tem a coleção?
SELECT COUNT(*) "Total em suporte MP3"
FROM musica m
INNER JOIN titulo t 
	ON m.id_titulo = t.id_titulo
INNER JOIN suporte s
	ON t.id_suporte = s.id_suporte
WHERE s.nome = 'MP3';

-- [o] Destes mp3 quais são o títulos cujo género é: Pop Rock?
SELECT COUNT(*) "Total MP3 do genero Pop Rock"
FROM musica m
INNER JOIN titulo t 
	ON m.id_titulo = t.id_titulo
INNER JOIN suporte s
	ON t.id_suporte = s.id_suporte
INNER JOIN genero g
	ON t.id_genero = g.id_genero
WHERE s.nome = 'MP3' AND g.nome = 'Pop Rock';

-- [p] Qual o custo total com “Blue-Ray”?
SELECT SUM(t.preco) "Custo total com Blue-Ray"
FROM titulo t 
INNER JOIN suporte s
	ON t.id_suporte = s.id_suporte
WHERE s.nome = 'Blue-Ray';

-- [q] Qual o custo total com “Blue-Ray” cuja editora é a EMI?
SELECT SUM(t.preco) "Custo com Blue-Ray"
FROM titulo t 
INNER JOIN suporte s
	ON t.id_suporte = s.id_suporte
INNER JOIN editora e
	ON t.id_editora = e.id_editora
WHERE s.nome = 'Blue-Ray' AND e.nome = 'EMI';

-- [r] Qual o património total dos títulos da coleção?
SELECT SUM(preco) Patrimonio FROM titulo;

-- [s] Qual a editora na qual o colecionador investiu mais dinheiro?
SELECT e.nome
FROM titulo t
INNER JOIN editora e
	ON t.id_editora = e.id_editora
GROUP BY e.nome
ORDER BY SUM(preco) DESC
FETCH FIRST 1 ROWS ONLY;

-- [t] Qual a editora que possui mais títulos de “Heavy Metal” na coleção? Quanto titulo possui essa editora?
SELECT e.nome
FROM titulo t
INNER JOIN editora e
	ON t.id_editora = e.id_editora
INNER JOIN genero g
	ON t.id_genero = g.id_genero
WHERE g.nome = 'Heavy Metal'
GROUP BY e.nome
ORDER BY COUNT(*) DESC
FETCH FIRST 1 ROWS ONLY;
