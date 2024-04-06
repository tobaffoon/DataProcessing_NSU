SELECT * FROM seats as s
JOIN boarding_passes as bp
	ON s.seat_no=bp.seat_no
JOIN ticket_flights as tf
	ON tf.ticket_no=bp.ticket_no AND tf.flight_id=bp.flight_id
JOIN flights as f
	ON f.flight_id=tf.flight_id AND f.aircraft_code=s.aircraft_code
WHERE s.fare_conditions='Economy+' --AND tf.fare_conditions<>'Economy'