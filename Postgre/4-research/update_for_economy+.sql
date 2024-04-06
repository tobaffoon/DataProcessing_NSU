UPDATE ticket_flights as tf SET fare_conditions='Economy+'
FROM seats as s
JOIN boarding_passes as bp
	ON s.seat_no=bp.seat_no
JOIN flights as f
	ON f.flight_id=bp.flight_id AND f.aircraft_code=s.aircraft_code
WHERE tf.ticket_no=bp.ticket_no AND tf.flight_id=bp.flight_id 
	AND s.fare_conditions='Economy+'