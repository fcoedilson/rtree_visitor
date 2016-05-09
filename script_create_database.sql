CREATE TABLE tb_dadorecebido
(
  coddadorecebido serial NOT NULL,
  imei character varying,
  datatransmissao timestamp without time zone,
  dadorecebido character varying(16000),
  CONSTRAINT tb_dadorebido_pk PRIMARY KEY (coddadorecebido)
);
ALTER TABLE tb_dadorecebido OWNER TO sgf;

CREATE TABLE tb_rastreador
(
  imei character varying NOT NULL,
  modelo character varying NOT NULL,
  datacadastro date NOT NULL,
  fabricante character varying,
  serial character varying,
  descchip character varying,
  numero character varying NOT NULL,
  CONSTRAINT tb_rastreador_pk PRIMARY KEY (imei)
);
ALTER TABLE tb_rastreador OWNER TO sgf;


CREATE TABLE tb_cadveiculo
(
  codveiculo integer NOT NULL DEFAULT nextval('codveiculo_seq'::regclass),
  codua integer,
  codespecie integer,
  codmodelo integer,
  placa character varying(10) NOT NULL,
  chassi character varying(30) NOT NULL,
  serie character varying(30),
  temseguro integer NOT NULL DEFAULT 0,
  status integer NOT NULL DEFAULT 0,
  combustivel character varying(15),
  propriedade character varying(50),
  valoraquisicao double precision,
  numtombamento integer,
  renavam character varying(50),
  cor character varying(20),
  estacionamento character varying(50),
  kmimplantacao integer,
  anomodelo integer,
  anofab integer,
  cod_ua_asi character varying(16),
  cod_patrimonio character varying(20),
  km_padrao_troca_lub integer,
  km_atual numeric,
  consumo_medio numeric,
  dt_cadastro timestamp without time zone,
  objetivo text,
  CONSTRAINT pk_veiculo PRIMARY KEY (codveiculo),
  CONSTRAINT fk_especie FOREIGN KEY (codespecie) REFERENCES tb_especie (codespecie) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_modelo FOREIGN KEY (codmodelo) REFERENCES tb_modelo (codmodelo) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ua_veiculo FOREIGN KEY (cod_ua_asi) REFERENCES tb_ua (cod_ua) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_chassi UNIQUE (chassi),
  CONSTRAINT uk_placa UNIQUE (placa)
);
ALTER TABLE tb_cadveiculo OWNER TO sgf;
GRANT ALL ON TABLE tb_cadveiculo TO sgf;

CREATE TABLE tb_veiculorastreador
(
  codveiculo integer NOT NULL,
  imei character varying NOT NULL,
  dataatualizacao timestamp without time zone NOT NULL,
  CONSTRAINT tb_veiculorastreador_pk PRIMARY KEY (codveiculo, imei),
  CONSTRAINT tb_cadveiculo_fk FOREIGN KEY (codveiculo)
      REFERENCES tb_cadveiculo (codveiculo) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tb_rastreador_fk FOREIGN KEY (imei)
      REFERENCES tb_rastreador (imei) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE tb_veiculorastreador OWNER TO sgf;


CREATE TABLE tb_dadolido
(
  coddadolido integer NOT NULL DEFAULT nextval('tb_dadoformatado_coddadoformat_seq'::regclass),
  coddadorecebido integer,
  imei character varying,
  statusrastreador character varying,
  datatransmissao timestamp without time zone,
  dataleitura timestamp without time zone,
  phoneadmin character varying,
  sinalgps character varying,
  minsec character varying,
  command character varying,
  latitude double precision,
  longitude double precision,
  speed double precision,
  direction double precision,
  entry1 integer,
  entry2 integer,
  entry3 integer,
  entry4 integer,
  codveiculo integedatacadastror,
  CONSTRAINT tb_dadoformatado_pkey PRIMARY KEY (coddadolido),
  CONSTRAINT tb_veiculorastreador_fk FOREIGN KEY (codveiculo, imei)
      REFERENCES tb_veiculorastreador (codveiculo, imei) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);
ALTER TABLE tb_dadolido OWNER TO sgf;


CREATE TABLE tb_transmissao
(
  codtransmissao serial NOT NULL,
  codveiculo integer NOT NULL DEFAULT 89,
  codponto integer,
  geomponto geometry,
  data_transmissao timestamp without time zone,
  dist_ponto real,
  odometro real,
  sta_entrada2 boolean,
  sta_entrada3 boolean,
  sta_entrada4 boolean,
  entrada2 smallint,
  entrada3 smallint,
  entrada4 smallint,
  sta_ignicao boolean,
  sta_panico boolean,
  temperatura real,
  velocidade real,
  combustivel real,
  x double precision,
  y double precision,
  CONSTRAINT pktransmissao PRIMARY KEY (codtransmissao),
  CONSTRAINT fk48f58ad9fc696cba FOREIGN KEY (codveiculo) REFERENCES tb_cadveiculo (codveiculo) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fktbponto2 FOREIGN KEY (codponto) REFERENCES tb_ponto (codponto) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT enforce_dims_geomponto CHECK (st_ndims(geomponto) = 2),
  CONSTRAINT enforce_srid_geomponto CHECK (st_srid(geomponto) = (-1))
);
ALTER TABLE tb_transmissao OWNER TO sgf;
GRANT ALL ON TABLE tb_transmissao TO sgf;



CREATE TABLE tb_ultimatransmissao
(
  codveiculo integer NOT NULL,
  codponto integer,
  geomponto geometry,
  data_transmissao timestamp without time zone,
  dist_ponto real,
  odometro real,
  sta_entrada2 boolean,
  sta_entrada3 boolean,
  sta_entrada4 boolean,
  entrada2 smallint,
  entrada3 smallint,
  entrada4 smallint,
  sta_ignicao boolean,
  sta_panico boolean,
  temperatura real,
  velocidade real,
  combustivel real,
  x double precision,
  y double precision,
  CONSTRAINT ultimatransmissao_pkey PRIMARY KEY (codveiculo),
  CONSTRAINT fk_tbponto1 FOREIGN KEY (codponto) REFERENCES tb_ponto (codponto) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT tbcadveiculo2 FOREIGN KEY (codveiculo) REFERENCES tb_cadveiculo (codveiculo) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT enforce_dims_geomponto CHECK (st_ndims(geomponto) = 2),
  CONSTRAINT enforce_srid_geomponto CHECK (st_srid(geomponto) = (-1))
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tb_ultimatransmissao OWNER TO sgf;
GRANT ALL ON TABLE tb_ultimatransmissao TO sgf;


-- Trigger: insert_dadoformatado_transmissao_veiculo on tb_dadolido
-- DROP TRIGGER insert_dadoformatado_transmissao_veiculo ON tb_dadolido;


CREATE OR REPLACE FUNCTION insert_dadoformatado_transmissao_veiculo()
  RETURNS trigger AS
$BODY$
    DECLARE
    BEGIN
        IF (TG_OP = 'INSERT') THEN
	   INSERT INTO SGF.TB_TRANSMISSAO(DATA_TRANSMISSAO, GEOMPONTO, VELOCIDADE,Y, X)
	   VALUES (NEW.DATATRANSMISSAO, geomfromtext('POINT(' || NEW.LATITUDE || '  ' || NEW.LONGITUDE || ')'), NEW.SPEED, NEW.LATITUDE, NEW.LONGITUDE );
	   RETURN NEW;
        END IF;
        RETURN NULL;
    END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_dadoformatado_transmissao_veiculo() OWNER TO sgf;
GRANT EXECUTE ON FUNCTION insert_dadoformatado_transmissao_veiculo() TO sgf;

CREATE TRIGGER insert_dadoformatado_transmissao_veiculo
  AFTER INSERT OR UPDATE
  ON tb_dadolido
  FOR EACH ROW
  EXECUTE PROCEDURE insert_dadoformatado_transmissao_veiculo();
  

  
  
  