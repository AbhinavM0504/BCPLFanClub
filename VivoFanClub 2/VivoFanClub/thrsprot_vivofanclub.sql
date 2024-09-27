-- phpMyAdmin SQL Dump
-- version 4.9.7
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Oct 30, 2022 at 10:53 PM
-- Server version: 5.7.23-23
-- PHP Version: 7.4.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `thrsprot_vivofanclub`
--

-- --------------------------------------------------------

--
-- Table structure for table `fan_club_all_gift_data`
--

CREATE TABLE `fan_club_all_gift_data` (
  `id` int(11) NOT NULL,
  `gift_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gift_image` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gift_category` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gift_status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `fan_club_data`
--

CREATE TABLE `fan_club_data` (
  `id` int(11) NOT NULL,
  `token_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `imei_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `customer_image` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `invoice_image` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `geo_location_points` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `geo_location_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pin_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `model_purchase` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `old_phone_brand` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_brand_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_model_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_manufacturer_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_activation_time` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gift_detail` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gift_image` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ans_1_value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ans_2A_value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ans_2B_value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ans_3_value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ans_4A_value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ans_4B_value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ans_5_value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ans_6_value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `fan_club_gift_imei_table`
--

CREATE TABLE `fan_club_gift_imei_table` (
  `id` int(11) NOT NULL,
  `model` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `imei_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `gift_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `fan_club_otp_data`
--

CREATE TABLE `fan_club_otp_data` (
  `id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `number` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `otp` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `fan_club_winner_data`
--

CREATE TABLE `fan_club_winner_data` (
  `id` int(11) NOT NULL,
  `winners_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `form`
--

CREATE TABLE `form` (
  `id` int(11) NOT NULL,
  `name` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `storename` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `number` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `image`
--

CREATE TABLE `image` (
  `front _image` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `designation` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `store_search`
--

CREATE TABLE `store_search` (
  `id` int(11) NOT NULL,
  `shop_name` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone_number` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `upload`
--

CREATE TABLE `upload` (
  `imagename` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `Vba_list`
--

CREATE TABLE `Vba_list` (
  `id` int(11) NOT NULL,
  `vba_name` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `number` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `store_name` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `store_address` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `xclub_customer_first_service_table`
--

CREATE TABLE `xclub_customer_first_service_table` (
  `id` int(11) NOT NULL,
  `customer_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `imei_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_of_purchase` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `svc_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `part_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cost` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `after_discount_price` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `employee_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `employee_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `xclub_customer_fourth_service_table`
--

CREATE TABLE `xclub_customer_fourth_service_table` (
  `id` int(11) NOT NULL,
  `customer_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `imei_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_of_purchase` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `store_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `retailer_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vba_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_imei_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `amount` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount_amount` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `invoice_image` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `xclub_login_table`
--

CREATE TABLE `xclub_login_table` (
  `id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `imei_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_of_purchase` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `app_version` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `xclub_number`
--

CREATE TABLE `xclub_number` (
  `Id` int(11) NOT NULL,
  `xclub_number_series` text COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `xclub_otp_table`
--

CREATE TABLE `xclub_otp_table` (
  `id` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `otp` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `purpose` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `xclub_referral_table`
--

CREATE TABLE `xclub_referral_table` (
  `id` int(11) NOT NULL,
  `u_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `imei_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `date_of_purchase` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refer_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refer_contact_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refer_purchase_modal` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refer_imei_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refer_invoice_image` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refer_status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refer_points` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refer_reward` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refer_redeem` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `xclub_service_center_table`
--

CREATE TABLE `xclub_service_center_table` (
  `id` int(11) NOT NULL,
  `store_city` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `store_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `store_code` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `store_contact_no` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `store_address` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `store_contact_person` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `xclub_service_table`
--

CREATE TABLE `xclub_service_table` (
  `id` int(11) NOT NULL,
  `service_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_logo` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `service_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `xseries_customer_details`
--

CREATE TABLE `xseries_customer_details` (
  `Id` int(11) NOT NULL,
  `xseries_number` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `number` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `IMEI_number` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(250) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `fan_club_all_gift_data`
--
ALTER TABLE `fan_club_all_gift_data`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `fan_club_data`
--
ALTER TABLE `fan_club_data`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `fan_club_gift_imei_table`
--
ALTER TABLE `fan_club_gift_imei_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `fan_club_otp_data`
--
ALTER TABLE `fan_club_otp_data`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `fan_club_winner_data`
--
ALTER TABLE `fan_club_winner_data`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `form`
--
ALTER TABLE `form`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `store_search`
--
ALTER TABLE `store_search`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `Vba_list`
--
ALTER TABLE `Vba_list`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `xclub_customer_first_service_table`
--
ALTER TABLE `xclub_customer_first_service_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `xclub_customer_fourth_service_table`
--
ALTER TABLE `xclub_customer_fourth_service_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `xclub_login_table`
--
ALTER TABLE `xclub_login_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `xclub_number`
--
ALTER TABLE `xclub_number`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `xclub_otp_table`
--
ALTER TABLE `xclub_otp_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `xclub_referral_table`
--
ALTER TABLE `xclub_referral_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `xclub_service_center_table`
--
ALTER TABLE `xclub_service_center_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `xclub_service_table`
--
ALTER TABLE `xclub_service_table`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `xseries_customer_details`
--
ALTER TABLE `xseries_customer_details`
  ADD PRIMARY KEY (`Id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `fan_club_all_gift_data`
--
ALTER TABLE `fan_club_all_gift_data`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `fan_club_data`
--
ALTER TABLE `fan_club_data`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `fan_club_gift_imei_table`
--
ALTER TABLE `fan_club_gift_imei_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `fan_club_otp_data`
--
ALTER TABLE `fan_club_otp_data`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `fan_club_winner_data`
--
ALTER TABLE `fan_club_winner_data`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `form`
--
ALTER TABLE `form`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `store_search`
--
ALTER TABLE `store_search`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `Vba_list`
--
ALTER TABLE `Vba_list`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `xclub_customer_first_service_table`
--
ALTER TABLE `xclub_customer_first_service_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `xclub_customer_fourth_service_table`
--
ALTER TABLE `xclub_customer_fourth_service_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `xclub_login_table`
--
ALTER TABLE `xclub_login_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `xclub_number`
--
ALTER TABLE `xclub_number`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `xclub_otp_table`
--
ALTER TABLE `xclub_otp_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `xclub_referral_table`
--
ALTER TABLE `xclub_referral_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `xclub_service_center_table`
--
ALTER TABLE `xclub_service_center_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `xclub_service_table`
--
ALTER TABLE `xclub_service_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `xseries_customer_details`
--
ALTER TABLE `xseries_customer_details`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
