SELECT DISTINCT flights.flight_no, flights.aircraft_code, pricings4.seat_nos, pricings4.fare_conditions, pricings4.amount
FROM pricings4
JOIN flights
	ON flights.flight_no = pricings4.flight_no
WHERE --pricings4.fare_conditions = 'Economy' 
flights.flight_no='PG0064'
AND flights.aircraft_code NOT IN ('')
ORDER BY flights.aircraft_code