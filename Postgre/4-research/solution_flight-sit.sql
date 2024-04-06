SELECT flights.flight_no,
boarding_passes.seat_no, MAX(ticket_flights.amount), MIN(ticket_flights.amount)
FROM flights
JOIN ticket_flights
	ON flights.flight_id = ticket_flights.flight_id		
JOIN boarding_passes
	ON ticket_flights.flight_id=boarding_passes.flight_id 
		AND ticket_flights.ticket_no=boarding_passes.ticket_no
WHERE ticket_flights.fare_conditions='Economy'
GROUP BY flights.flight_no, boarding_passes.seat_no
HAVING MAX(ticket_flights.amount)<>MIN(ticket_flights.amount)
ORDER BY flights.flight_no--, ticket_flights.ticket_no--, ticket_flights.amount

--154748 and 154768 154751- cheap
--154845 and 154861 154783 - exp