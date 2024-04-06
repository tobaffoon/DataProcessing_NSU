update seats set fare_conditions='Economy+'
WHERE
	aircraft_code='SU9' AND seat_no IN ('6A','6C','6D','6E','6F')
	aircraft_code='773' AND seat_no IN ('25A','25B','25C','25H','25J','25K','38A','38B','38C','38H','38J','38K')
	aircraft_code='763' AND seat_no IN ('27A','27B','27G','27H','9A','9B','9G','9H')
	aircraft_code='733' AND seat_no IN ('11A','11B','11C','11D','11E','11F')
	aircraft_code='321' AND seat_no IN ('19B','19C','19D','19E','20A','20F','8A','8B','8C','8D','8E','8F')
	aircraft_code='319' AND seat_no IN ('8B','8C','8D','8E')
	aircraft_code='CR2' AND seat_no IN ('1A', '1B', '1C', '1D')