records = LOAD '2000.csv' USING PigStorage(',') AS (Year,Month,DayofMonth,DayOfWeek,DepTime,CRSDepTime,ArrTime, CRSArrTime,UniqueCarrier,FlightNum,TailNum,ActualElapsedTime, CRSElapsedTime,AirTime,ArrDelay,DepDelay,Origin,Dest, Distance:int,TaxiIn,TaxiOut,Cancelled,CancellationCode, Diverted,CarrierDelay,WeatherDelay,NASDelay,SecurityDelay, LateAircraftDelay);
milage_recs = GROUP records ALL;
tot_miles = FOREACH milage_recs GENERATE SUM(records.Distance); STORE tot_miles INTO '/home/vagrant/hadoop/totalmiles';
