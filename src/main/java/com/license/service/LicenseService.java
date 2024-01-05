package com.license.service;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import com.license.model.License;



public interface LicenseService {

	List<License> getAllLicenses();

	Optional<License> getLicenseById(Long id);

	License updateLicense(Long id, License updatedLicense);

	void deleteLicense(Long id);

	License createLicense(License license, HttpSession session);

	int getTotalDemoUsers();

	int getTotalActualUsers();

	List<License> getAllDemoLicenses();

	List<License> getAllActualLicenses();

	boolean isValidLicenseKey(String licenseKey);

	boolean isLicenseValid(String licenseKey);

}
