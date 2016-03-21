package com.emc.documentum.delegates;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.emc.documentum.dtos.DocumentCreation;
import com.emc.documentum.dtos.DocumentumDocument;
import com.emc.documentum.dtos.DocumentumFolder;
import com.emc.documentum.dtos.DocumentumObject;
import com.emc.documentum.dtos.NavigationObject;
import com.emc.documentum.exceptions.CabinetNotFoundException;
import com.emc.documentum.exceptions.DocumentCreationException;
import com.emc.documentum.exceptions.DocumentNotFoundException;
import com.emc.documentum.exceptions.DocumentumException;
import com.emc.documentum.exceptions.FolderCreationException;
import com.emc.documentum.exceptions.FolderNotFoundException;
import com.emc.documentum.model.JsonObject;
import com.emc.documentum.services.rest.DCRestRepositoryController;
import com.emc.documentum.transformation.ObjectMapper;
import com.emc.documentum.wrappers.DCRestAPIWrapper;

@Component("DocumentumRestDelegate")
public class DocumentumRestDelegate implements DocumentumDelegate {
	
	Logger log = Logger.getLogger(DCRestRepositoryController.class.getCanonicalName());
	@Autowired
	DCRestAPIWrapper dcAPI;
	
	/* (non-Javadoc)
	 * @see com.emc.documentum.delegates.DocumentumDelegate#createFolder(java.lang.String, java.lang.String)
	 */
	@Override
	public DocumentumFolder createFolder( String cabinetName,
			String folderName) throws FolderCreationException,CabinetNotFoundException {
		log.entering(DCRestRepositoryController.class.getSimpleName(), "CreateFolder");
		JsonObject cabinet;
		JsonObject folder;
		try {
			cabinet = dcAPI.getCabinet(cabinetName);
			folder = dcAPI.createFolder(cabinet, folderName);
			return ObjectMapper.convertCoreRSFolder(folder);
		} catch (CabinetNotFoundException  | FolderCreationException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		} 
		

	}
	
	/* (non-Javadoc)
	 * @see com.emc.documentum.delegates.DocumentumDelegate#createDocument(com.emc.documentum.dtos.DocumentCreation)
	 */
	@Override
	public DocumentumDocument createDocument(DocumentCreation docCreation) throws DocumentumException{
		log.entering(DCRestRepositoryController.class.getSimpleName(), "createDocument");
		JsonObject document;
		JsonObject folder;
		try {
			folder =  dcAPI.getFolderByPath(docCreation.getFolderPath());
			document = dcAPI.createDocument(folder, docCreation.getProperties());
			return ObjectMapper.convertCoreRSDocument(document);
		} catch (FolderNotFoundException | DocumentCreationException e ) {
			log.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		} 
	}

	/* (non-Javadoc)
	 * @see com.emc.documentum.delegates.DocumentumDelegate#getCabinetByName(java.lang.String)
	 */
	@Override
	public DocumentumFolder getCabinetByName(String cabinetName) throws CabinetNotFoundException {
		
		try {
			return ObjectMapper.convertCoreRSFolder(dcAPI.getCabinet(cabinetName));
		} catch (CabinetNotFoundException e) {
			log.log(Level.SEVERE,e.getMessage(),e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see com.emc.documentum.delegates.DocumentumDelegate#getObjectById(java.lang.String)
	 */
	@Override
	public DocumentumObject getObjectById(String cabinetId) throws CabinetNotFoundException {
		try {
			return ObjectMapper.convertCoreRSObject(dcAPI.getObjectById(cabinetId));
		} catch (Exception e) {
			log.log(Level.SEVERE,e.getMessage(),e);
			//TODO Object Not Found Exception
			throw new CabinetNotFoundException(cabinetId);
		}
	}

	/* (non-Javadoc)
	 * @see com.emc.documentum.delegates.DocumentumDelegate#getAllCabinets()
	 */
	@Override
	public ArrayList<NavigationObject> getAllCabinets() {
		try {
			return dcAPI.getAllCabinets();
		} catch (Exception e) {
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see com.emc.documentum.delegates.DocumentumDelegate#getChildren(java.lang.String)
	 */
	@Override
	public ArrayList<NavigationObject> getChildren(String folderId) {
		try {
			return dcAPI.getChildren(folderId);
		} catch (Exception e) {
			//TODO Object Not Found Exception
			throw e;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.emc.documentum.delegates.DocumentumDelegate#getDocumentContentById(java.lang.String)
	 */
	@Override
	public byte[] getDocumentContentById(String documentId) throws DocumentNotFoundException{
		return dcAPI.getDocumentContentById(documentId);
	}

	@Override
	public ArrayList<DocumentumObject> getDocumentByName(String name) {
		try {
			return ObjectMapper.convertCoreRSEntryList(dcAPI.getDocumentByName(name));
		} catch (DocumentNotFoundException e) {
			return new ArrayList<DocumentumObject>();
		}
		
	}
}
