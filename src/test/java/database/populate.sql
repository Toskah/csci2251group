##########################
# Populate slumlord table
##########################
INSERT INTO slumlord(slumlord_user_name, slumlord_first_name, slumlord_last_name, slumlord_dob)
VALUES('eagerbeaver', 'Eager', 'Beaver', '2019-07-20');
INSERT INTO slumlord(slumlord_user_name, slumlord_first_name, slumlord_last_name, slumlord_dob)
VALUES('presidentgw', 'George', 'Washington', '1732-02-22');
INSERT INTO slumlord(slumlord_user_name, slumlord_first_name, slumlord_last_name, slumlord_dob)
VALUES('eugene', 'Eugene', 'Powell', '1987-04-16');
INSERT INTO slumlord(slumlord_user_name, slumlord_first_name, slumlord_last_name, slumlord_dob)
VALUES('lar', 'Larissa', 'Wallis', '1993-10-12');

##########################
# Populate property table
##########################
INSERT INTO property(property_type, property_address, property_city_code, property_numRooms, property_numBrooms, property_garage_count, property_sqr_foot, 
property_frontY_sqr_foot, property_backY_sqr_foot, property_num_tenants, property_rental_fee, property_last_payment_date, property_owner_id, property_vacancy_ind)
VALUES('V', '101 Demo Rd.', 'SAF', 1, 1, 0, 800, 200, 0, 1, 1500, '2019-01-20', 'eagerbeaver', 'O');
INSERT INTO property(property_type, property_address, property_city_code, property_numRooms, property_numBrooms, property_garage_count, property_sqr_foot, 
property_frontY_sqr_foot, property_backY_sqr_foot, property_num_tenants, property_rental_fee, property_last_payment_date, property_owner_id, property_vacancy_ind)
VALUES('S', '10 Plumb Branch Street', 'ABQ', 4, 3, 1, 2300, 1500, 2350, 3, 1200, '2019-05-16', 'lar', 'V');
INSERT INTO property(property_type, property_address, property_city_code, property_numRooms, property_numBrooms, property_garage_count, property_sqr_foot, 
property_frontY_sqr_foot, property_backY_sqr_foot, property_num_tenants, property_rental_fee, property_last_payment_date, property_owner_id, property_vacancy_ind)
VALUES('A', '579 3rd Dr.', 'ROS', 2, 1, 0, 1025, 0, 0, 1, 1500, '2019-01-20', 'presidentgw', 'V');
INSERT INTO property(property_type, property_address, property_city_code, property_numRooms, property_numBrooms, property_garage_count, property_sqr_foot, 
property_frontY_sqr_foot, property_backY_sqr_foot, property_num_tenants, property_rental_fee, property_last_payment_date, property_owner_id, property_vacancy_ind)
VALUES('V', '844 School Street', 'ABQ', 3, 2, 1, 1740, 680, 2200, 1, 1500, '2019-07-30', 'eugene', 'V');

##########################
# Populate tenant table
##########################
INSERT INTO tenant(tenant_first_name, tenant_last_name, tenant_phone_number, tenant_dob, tenant_address, tenant_city, tenant_zipCode, tenant_property_id)
VALUES('John', 'Doe', '5055055050', '1905-08-30', '123 2nd St.', 'Albuquerque', '87110', 1);
INSERT INTO tenant(tenant_first_name, tenant_last_name, tenant_phone_number, tenant_dob, tenant_address, tenant_city, tenant_zipCode, tenant_property_id)
VALUES('Festus', 'Linwood', '5055609340', '1998-06-14', '8286 Green Hill Lane', 'Santa Fe', '87501', 2);
INSERT INTO tenant(tenant_first_name, tenant_last_name, tenant_phone_number, tenant_dob, tenant_address, tenant_city, tenant_zipCode, tenant_property_id)
VALUES('Damion', 'Ridley', '5059304821', '1968-11-09', '781 Lookout Ave.', 'Las Cruces', '88004', 2);
INSERT INTO tenant(tenant_first_name, tenant_last_name, tenant_phone_number, tenant_dob, tenant_address, tenant_city, tenant_zipCode, tenant_property_id)
VALUES('Chad', 'Peters', '4347004891', '1983-03-06', '229B Arch Rd.', 'Roswell', '88202', 2);
INSERT INTO tenant(tenant_first_name, tenant_last_name, tenant_phone_number, tenant_dob, tenant_address, tenant_city, tenant_zipCode, tenant_property_id)
VALUES('Kaitlin', 'Leavitt', '5054352678', '1979-12-21', '808 Cooper Ave.', 'Albuquerque', '87109', 3);
INSERT INTO tenant(tenant_first_name, tenant_last_name, tenant_phone_number, tenant_dob, tenant_address, tenant_city, tenant_zipCode, tenant_property_id)
VALUES('Jonathan', 'Backus', '5055055050', '1965-03-18', '9790 Wall Road ', 'Albuquerque', '87108', 4);