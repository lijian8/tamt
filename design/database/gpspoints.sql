-- Table: gpspoints

-- DROP TABLE gpspoints;

CREATE TABLE gpspoints
(
  pid integer NOT NULL,
  id text NOT NULL,
  gpstraceid text NOT NULL,
  latitude double precision NOT NULL,
  longitude double precision NOT NULL,
  bearing double precision NOT NULL,
  speed double precision NOT NULL,
  altitude double precision NOT NULL,
  created timestamp without time zone NOT NULL,
  geometry geometry,
  tag_id text,
  road_id text,
  zone_id text,
  daytype text,
  "hour" text,
  kph double precision,
  speedbinnumber integer,
  CONSTRAINT gpspoints_pkey PRIMARY KEY (pid),
  CONSTRAINT enforce_dims_geometry CHECK (st_ndims(geometry) = 2),
  CONSTRAINT enforce_geotype_geometry CHECK (geometrytype(geometry) = 'POINT'::text OR geometry IS NULL),
  CONSTRAINT enforce_srid_geometry CHECK (st_srid(geometry) = 4326)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE gpspoints OWNER TO gis;

-- Index: idx_gpspoints_geometry

-- DROP INDEX idx_gpspoints_geometry;

CREATE INDEX idx_gpspoints_geometry
  ON gpspoints
  USING gist
  (geometry);

-- Index: idx_gpspoints_timeasc

-- DROP INDEX idx_gpspoints_timeasc;

CREATE INDEX idx_gpspoints_timeasc
  ON gpspoints
  USING btree
  (created);

