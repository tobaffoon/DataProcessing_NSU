DROP TABLE pricings4

CREATE TABLE pricings4 AS
SELECT flights.flight_no, ticket_flights.fare_conditions, ticket_flights.amount
FROM flights
JOIN ticket_flights
	ON flights.flight_id = ticket_flights.flight_id		
JOIN boarding_passes
	ON ticket_flights.flight_id=boarding_passes.flight_id 
		AND ticket_flights.ticket_no=boarding_passes.ticket_no -- w/o it counts all fare conditions
GROUP BY flights.flight_no, ticket_flights.fare_conditions, ticket_flights.amount
ORDER BY flights.flight_no