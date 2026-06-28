-- Seed sample data for testing
-- Passwords are hashed using BCrypt. All passwords are: "password123" except superadmin which is "superadmin123"

-- Insert sample users
INSERT INTO users (email, password_hash, first_name, last_name, phone, role_id, is_active, is_blocked) VALUES
-- Super Admin (password: superadmin123)
('superadmin@salon.com', '$2a$12$41AnV04QBo2J9OPRLkbPoOUcxTy2nfbkBsql49PFPJbgMqiBjecBC', 'Super', 'Admin', '0000000000', 1, true, false),

-- Salon Owners (password: password123)
('owner1@salon.com', '$2a$12$41AnV04QBo2J9OPRLkbPoOUcxTy2nfbkBsql49PFPJbgMqiBjecBC', 'John', 'Smith', '9876543210', 2, true, false),
('owner2@salon.com', '$2a$12$41AnV04QBo2J9OPRLkbPoOUcxTy2nfbkBsql49PFPJbgMqiBjecBC', 'Sarah', 'Johnson', '9876543211', 2, true, false),

-- Barbers (password: password123)
('barber1@salon.com', '$2a$12$41AnV04QBo2J9OPRLkbPoOUcxTy2nfbkBsql49PFPJbgMqiBjecBC', 'Mike', 'Wilson', '9876543212', 3, true, false),
('barber2@salon.com', '$2a$12$41AnV04QBo2J9OPRLkbPoOUcxTy2nfbkBsql49PFPJbgMqiBjecBC', 'David', 'Brown', '9876543213', 3, true, false),
('barber3@salon.com', '$2a$12$41AnV04QBo2J9OPRLkbPoOUcxTy2nfbkBsql49PFPJbgMqiBjecBC', 'James', 'Lee', '9876543214', 3, true, false),

-- Customers (password: password123)
('customer1@email.com', '$2a$12$41AnV04QBo2J9OPRLkbPoOUcxTy2nfbkBsql49PFPJbgMqiBjecBC', 'Robert', 'Taylor', '9876543215', 4, true, false),
('customer2@email.com', '$2a$12$41AnV04QBo2J9OPRLkbPoOUcxTy2nfbkBsql49PFPJbgMqiBjecBC', 'Emily', 'Davis', '9876543216', 4, true, false),
('customer3@email.com', '$2a$12$41AnV04QBo2J9OPRLkbPoOUcxTy2nfbkBsql49PFPJbgMqiBjecBC', 'Michael', 'Miller', '9876543217', 4, true, false);

-- Insert sample salons
INSERT INTO salons (owner_id, name, description, address, city, pincode, phone, email, is_active, is_suspended, rating, total_reviews) VALUES
                                                                                                                                           ((SELECT id FROM users WHERE email = 'owner1@salon.com'), 'Premium Cuts Salon', 'Best salon in town with expert stylists and modern equipment', '123 Main Street', 'New York', '10001', '5551234567', 'contact@premiumcuts.com', true, false, 4.5, 120),
                                                                                                                                           ((SELECT id FROM users WHERE email = 'owner2@salon.com'), 'Style Studio', 'Modern styling and grooming services with premium products', '456 Oak Avenue', 'Los Angeles', '90001', '5559876543', 'info@stylestudio.com', true, false, 4.2, 85);

-- Insert barbers linked to salons
INSERT INTO barbers (user_id, salon_id, specialization, experience_years, is_active, rating, total_reviews) VALUES
                                                                                                                ((SELECT id FROM users WHERE email = 'barber1@salon.com'), (SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 'Haircut, Beard, Styling, Coloring', 8, true, 4.6, 95),
                                                                                                                ((SELECT id FROM users WHERE email = 'barber2@salon.com'), (SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 'Haircut, Fade, Beard Trim', 5, true, 4.3, 60),
                                                                                                                ((SELECT id FROM users WHERE email = 'barber3@salon.com'), (SELECT id FROM salons WHERE name = 'Style Studio'), 'Haircut, Styling, Facial', 10, true, 4.8, 110);

-- Insert services for each salon
INSERT INTO services (salon_id, name, description, duration_minutes, price, category_id, is_active) VALUES
-- Premium Cuts Salon services
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 'Classic Haircut', 'Traditional haircut with scissors and clippers', 30, 25.00, 1, true),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 'Skin Fade', 'Modern skin fade with precision styling', 45, 35.00, 1, true),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 'Beard Trim & Shape', 'Professional beard trimming and shaping', 20, 15.00, 2, true),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 'Full Shave', 'Traditional straight razor shave with hot towel', 30, 20.00, 2, true),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 'Haircut & Beard Combo', 'Complete haircut and beard grooming package', 50, 40.00, 3, true),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 'Premium Facial', 'Deep cleansing facial with massage', 60, 50.00, 4, true),

-- Style Studio services
((SELECT id FROM salons WHERE name = 'Style Studio'), 'Executive Cut', 'Premium haircut with consultation', 40, 45.00, 1, true),
((SELECT id FROM salons WHERE name = 'Style Studio'), 'Designer Fade', 'Custom fade design with artistic styling', 50, 55.00, 1, true),
((SELECT id FROM salons WHERE name = 'Style Studio'), 'Luxury Beard Service', 'Beard trim, shape, and oil treatment', 30, 25.00, 2, true),
((SELECT id FROM salons WHERE name = 'Style Studio'), 'Gentleman''s Shave', 'Traditional shave with premium products', 35, 30.00, 2, true),
((SELECT id FROM salons WHERE name = 'Style Studio'), 'Complete Grooming Package', 'Haircut, beard, and facial combo', 90, 85.00, 3, true),
((SELECT id FROM salons WHERE name = 'Style Studio'), 'Rejuvenating Facial', 'Anti-aging facial treatment', 75, 70.00, 4, true);

-- Insert working hours for salons
INSERT INTO working_hours (salon_id, day_of_week, opening_time, closing_time, is_closed) VALUES
-- Premium Cuts Salon (Mon-Sat 9AM-7PM, Sun closed)
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 1, '09:00:00', '19:00:00', false),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 2, '09:00:00', '19:00:00', false),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 3, '09:00:00', '19:00:00', false),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 4, '09:00:00', '19:00:00', false),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 5, '09:00:00', '19:00:00', false),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 6, '10:00:00', '18:00:00', false),
((SELECT id FROM salons WHERE name = 'Premium Cuts Salon'), 7, '00:00:00', '00:00:00', true),

-- Style Studio (Mon-Fri 10AM-8PM, Sat 9AM-6PM, Sun closed)
((SELECT id FROM salons WHERE name = 'Style Studio'), 1, '10:00:00', '20:00:00', false),
((SELECT id FROM salons WHERE name = 'Style Studio'), 2, '10:00:00', '20:00:00', false),
((SELECT id FROM salons WHERE name = 'Style Studio'), 3, '10:00:00', '20:00:00', false),
((SELECT id FROM salons WHERE name = 'Style Studio'), 4, '10:00:00', '20:00:00', false),
((SELECT id FROM salons WHERE name = 'Style Studio'), 5, '10:00:00', '20:00:00', false),
((SELECT id FROM salons WHERE name = 'Style Studio'), 6, '09:00:00', '18:00:00', false),
((SELECT id FROM salons WHERE name = 'Style Studio'), 7, '00:00:00', '00:00:00', true);

-- Insert availability slots for barbers (next 7 days)
INSERT INTO availability_slots (barber_id, slot_date, start_time, end_time, is_available, is_blocked) VALUES
-- Barber 1 (Mike) - Premium Cuts Salon
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber1@salon.com')), CURRENT_DATE + INTERVAL '1 day', '09:00:00', '10:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber1@salon.com')), CURRENT_DATE + INTERVAL '1 day', '10:00:00', '11:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber1@salon.com')), CURRENT_DATE + INTERVAL '1 day', '11:00:00', '12:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber1@salon.com')), CURRENT_DATE + INTERVAL '1 day', '14:00:00', '15:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber1@salon.com')), CURRENT_DATE + INTERVAL '1 day', '15:00:00', '16:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber1@salon.com')), CURRENT_DATE + INTERVAL '2 day', '09:00:00', '10:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber1@salon.com')), CURRENT_DATE + INTERVAL '2 day', '10:00:00', '11:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber1@salon.com')), CURRENT_DATE + INTERVAL '2 day', '14:00:00', '15:00:00', true, false),

-- Barber 2 (David) - Premium Cuts Salon
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber2@salon.com')), CURRENT_DATE + INTERVAL '1 day', '09:00:00', '10:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber2@salon.com')), CURRENT_DATE + INTERVAL '1 day', '11:00:00', '12:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber2@salon.com')), CURRENT_DATE + INTERVAL '1 day', '15:00:00', '16:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber2@salon.com')), CURRENT_DATE + INTERVAL '2 day', '10:00:00', '11:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber2@salon.com')), CURRENT_DATE + INTERVAL '2 day', '14:00:00', '15:00:00', true, false),

-- Barber 3 (James) - Style Studio
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber3@salon.com')), CURRENT_DATE + INTERVAL '1 day', '10:00:00', '11:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber3@salon.com')), CURRENT_DATE + INTERVAL '1 day', '11:00:00', '12:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber3@salon.com')), CURRENT_DATE + INTERVAL '1 day', '14:00:00', '15:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber3@salon.com')), CURRENT_DATE + INTERVAL '1 day', '15:00:00', '16:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber3@salon.com')), CURRENT_DATE + INTERVAL '2 day', '10:00:00', '11:00:00', true, false),
((SELECT id FROM barbers WHERE user_id = (SELECT id FROM users WHERE email = 'barber3@salon.com')), CURRENT_DATE + INTERVAL '2 day', '11:00:00', '12:00:00', true, false);