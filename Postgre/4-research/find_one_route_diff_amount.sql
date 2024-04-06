SELECT flights.flight_id, flights.flight_no, flights.departure_airport, flights.arrival_airport, ticket_flights.fare_conditions, ticket_flights.amount
FROM flights 
INNER JOIN ticket_flights 
	ON flights.flight_id=ticket_flights.flight_id
WHERE ticket_flights.amount=12300.00 OR ticket_flights.amount=13500.00
ORDER BY flights.flight_no
--PG0012 cheaper and more expensive 
--154748
--154845