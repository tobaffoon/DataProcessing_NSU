SELECT flights.flight_no, flights.departure_airport, flights.arrival_airport, ticket_flights.fare_conditions, COUNT(DISTINCT ticket_flights.amount)
FROM flights 
INNER JOIN ticket_flights 
	ON flights.flight_id=ticket_flights.flight_id 
GROUP BY flights.flight_no, flights.departure_airport, flights.arrival_airport, ticket_flights.fare_conditions
HAVING COUNT(DISTINCT ticket_flights.amount) > 2
ORDER BY flights.flight_no