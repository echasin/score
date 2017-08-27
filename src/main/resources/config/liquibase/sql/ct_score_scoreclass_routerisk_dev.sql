
--1. For  a specific Scoreclass = 'Route Risk'
--2. Get the array possible columns for grid.  This will hard coded for section 5.0

/* Select distinct  sty.nameshort as SCORECATEGORY 
from scoreclass scl, scorecategory sca, scoretype sty
where 
sca.scoreclass_id = scl.id
and sty.scorecategory_id = sca.id
and scl.name = 'Route Risk'
*/

--3. Create view for Scoreclass = "Route Risk (DEV)"


--DROP VIEW v_score_scoreclass_routerisk_dev;
CREATE or REPLACE VIEW v_score
AS
SELECT s.id as SCOREID, scl.nameshort as SCORECLASS,  sca.nameshort as SCORECATEGORY, sty.nameshort as SCORETYPE, s.scorevalue as SCOREVALUE
FROM score s, scoreclass scl, scorecategory sca, scoretype sty
WHERE s.scoreclass_id = scl.id
AND s.scorecategory_id = sca.id
AND s.scoretype_id = sty.id
;

/*
--4. Set variable for columns for crosstab.  This is creating error which will require hardcoding of values
/*
DECLARE @scoreclasscolumns; 
SET   @scoreclasscolumns = 'SCORETYPE = ''RRTYPE1'' or SCORETYPE = ''RRTYPE2'' or SCORETYPE = ''RRTYPE3''';
PRINT @scoreclasscolumns;
*/

--5. Create crosstab view for Scoreclass = "Route Risk"
*/
--DROP VIEW ct_score_scoreclass_routerisk_dev;
CREATE or REPLACE VIEW ct_score_scoreclass_routerisk_dev
AS
SELECT * From crosstab(
'SELECT SCOREID,SCORECATEGORY, SCORETYPE, SCOREVALUE
   from v_score
   ORDER BY SCOREID,SCORETYPE'
   ,$$VALUES ('RR TYPE1'),('RR TYPE2')$$)
AS (SCOREID bigint,SCORECATEGORY varchar, RRTYPE1 varchar, RRTYPE2 varchar);

--6.
SELECT * FROM  v_score;
SELECT * FROM  ct_score_scoreclass_routerisk_dev;

