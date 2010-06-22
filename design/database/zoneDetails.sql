-- Table: "zoneDetails"

-- DROP TABLE "zoneDetails";

CREATE TABLE "zoneDetails"
(
  id text NOT NULL,
  "name" text NOT NULL,
  description text,
  region text NOT NULL,
  zonetype text NOT NULL,
  geometry geometry,
  CONSTRAINT "zoneDetails_pkey" PRIMARY KEY (id),
  CONSTRAINT unique_zone_name UNIQUE (name),
  CONSTRAINT enforce_dims_geometry CHECK (st_ndims(geometry) = 2),
  CONSTRAINT enforce_geotype_geometry CHECK (geometrytype(geometry) = 'LINESTRING'::text OR geometry IS NULL),
  CONSTRAINT enforce_srid_geometry CHECK (st_srid(geometry) = 4326)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "zoneDetails" OWNER TO gis;
