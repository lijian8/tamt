-- Table: speeddistributiontrafficflowtagvehiclespeed

-- DROP TABLE speeddistributiontrafficflowtagvehiclespeed;

CREATE TABLE speeddistributiontrafficflowtagvehiclespeed
(
  pid integer NOT NULL,
  regionid text NOT NULL,
  tagid text NOT NULL,
  vehicletype text NOT NULL,
  speedbin integer,
  vehiclesecondsperbinperyear double precision,
  vehiclemeterssperbinperyear double precision,
  fractionalaveragespeed double precision,
  fractionalvehiclesecondsperbinperyear double precision,
  fractionalvehiclemeterssperbinperyear double precision,
  CONSTRAINT speeddistributiontrafficflowtagvehiclespeed_pkey PRIMARY KEY (pid),
  CONSTRAINT speeddistributiontrafficflowtagvehiclespeed_regionid_fkey FOREIGN KEY (regionid)
      REFERENCES studyregion (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT speeddistributiontrafficflowtagvehiclespeed_tagid_fkey FOREIGN KEY (tagid)
      REFERENCES tagdetails (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE speeddistributiontrafficflowtagvehiclespeed OWNER TO gis;
