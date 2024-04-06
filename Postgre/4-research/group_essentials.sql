SELECT flights.flight_no, ticket_flights.fare_conditions, ticket_flights.amount, COUNT(*)
FROM flights 
INNER JOIN ticket_flights 
	ON flights.flight_id=ticket_flights.flight_id 
GROUP BY flights.flight_no, ticket_flights.fare_conditions, ticket_flights.amount