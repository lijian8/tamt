-- Table: speeddistributiontrafficflowvehiclespeedfraction

-- DROP TABLE speeddistributiontrafficflowvehiclespeedfraction;

CREATE TABLE speeddistributiontrafficflowvehiclespeedfraction
(
  pid integer NOT NULL,
  regionid text NOT NULL,
  vehicletype text NOT NULL,
  speedbin integer,
  fractionalseconds double precision,
  fractionalmeters double precision,
  averagespeed double precision,
  CONSTRAINT speeddistributiontrafficflowvehiclespeedfraction_pkey PRIMARY KEY (pid),
  CONSTRAINT speeddistributiontrafficflowvehiclespeedfraction_regionid_fkey FOREIGN KEY (regionid)
      REFERENCES studyregion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE speeddistributiontrafficflowvehiclespeedfraction OWNER TO gis;
