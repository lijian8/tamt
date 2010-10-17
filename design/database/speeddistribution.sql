-- Table: speeddistribution

-- DROP TABLE speeddistribution;

CREATE TABLE speeddistribution
(
  pid integer NOT NULL,
  regionid text NOT NULL,
  tagid text NOT NULL,
  daytype text NOT NULL,
  hourbin integer,
  speedbin integer,
  timeinbin double precision,
  distanceinbin double precision,
  fractionaltimeinbin double precision,
  fractionaldistanceinbin double precision,
  CONSTRAINT speeddistribution_pkey PRIMARY KEY (pid),
  CONSTRAINT speeddistribution_regionid_fkey FOREIGN KEY (regionid)
      REFERENCES studyregion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT speeddistribution_tagid_fkey FOREIGN KEY (tagid)
      REFERENCES tagdetails (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE speeddistribution OWNER TO gis;

