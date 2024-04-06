SELECT DISTINCT flights.flight_no, flights.aircraft_code, pricings4.amount, pricings4.seat_nos
FROM pricings4
JOIN flights
	ON flights.flight_no = pricings4.flight_no
WHERE pricings4.fare_conditions = 'Economy' 
AND flights.aircraft_code NOT IN ('CR2', '319', '321', '733', '763', '773', 'CN1', 'SU9')
ORDER BY flights.aircraft_code