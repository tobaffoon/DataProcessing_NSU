UPDATE ticket_flights as tf SET fare_conditions='Economy+'
FROM boarding_passes as bp
	JOIN seats as s
		ON s.seat_no=bp.seat_no AND s.aircraft_code='CR2' AND s.seat_no in ('1A', '1B', '1C', '1D')
	WHERE bp.flight_id=tf.flight_id 