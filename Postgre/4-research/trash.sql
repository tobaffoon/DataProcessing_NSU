SELECT *, flights.scheduled_departure-bookings.book_date
FROM flights 
JOIN ticket_flights
	ON flights.flight_id = ticket_flights.flight_id
JOIN tickets
	ON ticket_flights.ticket_no=tickets.ticket_no
JOIN bookings
	ON tickets.book_ref=bookings.book_ref
JOIN boarding_passes
	ON ticket_flights.flight_id=boarding_passes.flight_id
WHERE ticket_flights.fare_conditions='Economy'
--ORDER BY ticket_flights.amount
ORDER BY flights.flight_no, bookings.book_ref, ticket_flights.amount
LIMIT 1000

--154748 and 154768 154751- cheap
--154845 and 154861 154783 - exp