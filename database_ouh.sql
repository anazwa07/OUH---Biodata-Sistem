-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 06, 2026 at 10:55 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `database_ouh`
--

-- --------------------------------------------------------

--
-- Table structure for table `developer`
--

CREATE TABLE `developer` (
  `id` int(11) NOT NULL,
  `nama` varchar(150) NOT NULL,
  `nim` varchar(50) NOT NULL,
  `kelas` varchar(50) NOT NULL,
  `foto_path` varchar(512) DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `developer`
--

INSERT INTO `developer` (`id`, `nama`, `nim`, `kelas`, `foto_path`) VALUES
(4, 'Khrysdyan Elga Rexsa Purba', '2503311893', 'IF A SG', 'C:\\Users\\user\\Downloads\\WhatsApp Image 2026-05-31 at 22.10.14.jpeg'),
(5, 'Zahara Azima', '2503310783', 'IF A SG', ''),
(6, 'Dewy Sheilla Pratama', '2503311480', 'IF A SG', ''),
(7, 'Nursaybah Kirani Br Sembiring', '2503311175', 'IF A SG', ''),
(8, 'Dinda Syahira', '2503311959', 'IF A SG', ''),
(9, 'Nurul Fadila Ananda', '2503311547', 'IF A SG', ''),
(10, 'Putri Wandha Hafizah', '2503311785', 'IF A SG', ''),
(11, 'Andini Azhara Waruwu', '2503311889', 'IF A SG', '');

-- --------------------------------------------------------

--
-- Table structure for table `orangutan`
--

CREATE TABLE `orangutan` (
  `id` int(11) NOT NULL,
  `nama` varchar(100) NOT NULL,
  `umur` int(11) NOT NULL,
  `jenis_kelamin` enum('Jantan','Betina') NOT NULL,
  `status_konservasi` varchar(50) DEFAULT 'Kritis',
  `deskripsi` text DEFAULT NULL,
  `tanggal_diinput` timestamp NOT NULL DEFAULT current_timestamp(),
  `foto` varchar(255) DEFAULT NULL,
  `spesies` varchar(100) DEFAULT NULL,
  `berat_badan` double DEFAULT NULL,
  `tinggi_badan` double DEFAULT NULL,
  `lokasi_habitat` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orangutan`
--

INSERT INTO `orangutan` (`id`, `nama`, `umur`, `jenis_kelamin`, `status_konservasi`, `deskripsi`, `tanggal_diinput`, `foto`, `spesies`, `berat_badan`, `tinggi_badan`, `lokasi_habitat`) VALUES
(1, 'Dinda', 18, 'Betina', 'Dikurung', 'Suka Makan Rumput', '2026-05-26 16:31:57', 'C:\\Users\\user\\Pictures\\Screenshots\\Screenshot 2026-06-04 000939.png', 'Pongo', 144, 1.5, 'Hutan Hujan Tropis');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('admin','user') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `role`) VALUES
(1, 'admin', 'superadmin', 'admin'),
(2, 'user', 'user123', 'user');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `developer`
--
ALTER TABLE `developer`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orangutan`
--
ALTER TABLE `orangutan`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `developer`
--
ALTER TABLE `developer`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `orangutan`
--
ALTER TABLE `orangutan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
