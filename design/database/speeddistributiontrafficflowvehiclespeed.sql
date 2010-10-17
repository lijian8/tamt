-- Table: speeddistributiontrafficflowvehiclespeed

-- DROP TABLE speeddistributiontrafficflowvehiclespeed;

CREATE TABLE speeddistributiontrafficflowvehiclespeed
(
  pid integer NOT NULL,
  regionid text NOT NULL,
  vehicletype text NOT NULL,
  speedbin integer,
  totalseconds double precision,
  totalmeters double precision,
  averagespeed double precision,
  CONSTRAINT speeddistributiontrafficflowvehiclespeed_pkey PRIMARY KEY (pid),
  CONSTRAINT speeddistributiontrafficflowvehiclespeed_regionid_fkey FOREIGN KEY (regionid)
      REFERENCES studyregion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE speeddistributiontrafficflowvehiclespeed OWNER TO gis;
