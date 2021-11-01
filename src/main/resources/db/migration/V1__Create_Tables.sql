-- MySql script
CREATE TABLE sequence (
	id bigint NOT NULL AUTO_INCREMENT,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	type varchar(50) NOT NULL,
	code bigint NOT NULL,
	CONSTRAINT pk_id PRIMARY KEY (id),
	UNIQUE (type,code)
);