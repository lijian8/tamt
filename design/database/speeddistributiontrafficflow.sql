-- Table: speeddistributiontrafficflow

-- DROP TABLE speeddistributiontrafficflow;

CREATE TABLE speeddistributiontrafficflow
(
  pid integer NOT NULL,
  regionid text NOT NULL,
  tagid text NOT NULL,
  daytype text NOT NULL,
  vehicletype text NOT NULL,
  speedbin integer,
  vehiclesecondsperday double precision,
  vehiclemeterssperday double precision,
  weightedaveragespeed double precision,
  fractionalvehiclesecondsperday double precision,
  fractionalvehiclemeterssperday double precision,
  CONSTRAINT speeddistributiontrafficflow_pkey PRIMARY KEY (pid),
  CONSTRAINT speeddistributiontrafficflow_regionid_fkey FOREIGN KEY (regionid)
      REFERENCES studyregion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT speeddistributiontrafficflow_tagid_fkey FOREIGN KEY (tagid)
      REFERENCES tagdetails (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE speeddistributiontrafficflow OWNER TO gis;
