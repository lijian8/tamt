-- Table: "tagDetails"

-- DROP TABLE "tagDetails";

CREATE TABLE "tagDetails"
(
  id text NOT NULL,
  "name" text NOT NULL,
  description text,
  region text NOT NULL,
  CONSTRAINT "tagDetails_pkey" PRIMARY KEY (id),
  CONSTRAINT unique_name UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE "tagDetails" OWNER TO gis;
