package com.circle.ayu.database.dao;

import static com.circle.ayu.utilities.UuidDictionary.ENCOUNTER_ADULTINITIAL;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.circle.ayu.app.AppConstants;
import com.circle.ayu.models.PrescriptionModel;
import com.circle.ayu.models.dto.VisitAttributeDTO;
import com.circle.ayu.models.dto.VisitAttribute_Speciality;
import com.circle.ayu.models.dto.VisitDTO;
import com.circle.ayu.utilities.DateAndTimeUtils;
import com.circle.ayu.utilities.Logger;
import com.circle.ayu.utilities.exception.DAOException;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


import java.util.ArrayList;
import java.util.List;

public class VisitsDAO {


    private long createdRecordsCount = 0;

    public boolean insertVisit(List<VisitDTO> visitDTOS) throws DAOException {
        boolean isInserted = true;
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        try {

            for (VisitDTO visit : visitDTOS) {
                createVisits(visit, db);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            isInserted = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return isInserted;
    }

    private boolean createVisits(VisitDTO visit, SQLiteDatabase db) throws DAOException {
        boolean isCreated = true;
        ContentValues values = new ContentValues();
        try {
            values.put("uuid", visit.getUuid());
            values.put("patientuuid", visit.getPatientuuid());
            values.put("locationuuid", visit.getLocationuuid());
            values.put("visit_type_uuid", visit.getVisitTypeUuid());
            values.put("creator", visit.getCreatoruuid());
            values.put("startdate", DateAndTimeUtils.formatDateFromOnetoAnother(visit.getStartdate(), "MMM dd, yyyy hh:mm:ss a", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            values.put("enddate", visit.getEnddate());
            values.put("modified_date", AppConstants.dateAndTimeUtils.currentDateTime());
            values.put("sync", visit.getSyncd());
            createdRecordsCount = db.insertWithOnConflict("tbl_visit", null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLException e) {
            isCreated = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
        }
        return isCreated;
    }


    public boolean insertPatientToDB(VisitDTO visit) throws DAOException {
        boolean isCreated = true;
        long createdRecordsCount1 = 0;
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        ContentValues values = new ContentValues();
        db.beginTransaction();
        List<VisitAttributeDTO> visitAttributeDTOS = new ArrayList<>();
        try {

            values.put("uuid", visit.getUuid());
            values.put("patientuuid", visit.getPatientuuid());
            values.put("locationuuid", visit.getLocationuuid());
            values.put("visit_type_uuid", visit.getVisitTypeUuid());
            values.put("creator", visit.getCreatoruuid());
            values.put("startdate", visit.getStartdate());
            values.put("enddate", visit.getEnddate());
            values.put("modified_date", AppConstants.dateAndTimeUtils.currentDateTime());
            values.put("sync", false);

            visitAttributeDTOS = visit.getVisitAttributeDTOS();
            if (visitAttributeDTOS != null) {
                insertVisitAttribToDB(visitAttributeDTOS, db);
            }

            createdRecordsCount1 = db.insert("tbl_visit", null, values);
            db.setTransactionSuccessful();
            Logger.logD("created records", "created records count" + createdRecordsCount1);
        } catch (SQLException e) {
            isCreated = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }
        return isCreated;

    }

    public boolean insertVisitAttribToDB(List<VisitAttributeDTO> visitAttributeDTOS, SQLiteDatabase db)
            throws DAOException {
        boolean isCreated = true;
        ContentValues values = new ContentValues();
        db.beginTransaction();
        try {
            for (VisitAttributeDTO visit : visitAttributeDTOS) {
                values.put("uuid", visit.getUuid());
                values.put("value", visit.getValue());
                values.put("visit_attribute_type_uuid", visit.getVisit_attribute_type_uuid());
                values.put("visituuid", visit.getVisit_uuid());
                values.put("modified_date", AppConstants.dateAndTimeUtils.currentDateTime());
                values.put("sync", "true");
                createdRecordsCount = db.insertWithOnConflict("tbl_visit_attribute", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
            Logger.logD("created records", "created records count" + createdRecordsCount);
        } catch (SQLException e) {
            isCreated = false;
            throw new DAOException(e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
        return isCreated;
    }


    //update condition for speciality
/*
    public boolean update_visitTbl_speciality(String spinner_value, String visitUUID) throws DAOException {
        boolean isupdatedone = false;
//        String cursor_uuid = "", cursor_value="";
        Log.d("SPINNER", "SPINNER_Selected_valuelogs: "+ spinner_value);
        Log.d("SPINNER", "SPINNER_Selected_uuidlogs: "+ visitUUID);

       */
/* SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        Cursor idCursor = db.rawQuery("SELECT value FROM tbl_dr_speciality WHERE value = ?",
                new String[]{spinner_value});

        if(idCursor.getCount() != 0)
        {
            while(idCursor.moveToNext())
            {
                 cursor_uuid = idCursor.getString(idCursor.getColumnIndexOrThrow("uuid"));
            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();*//*



        SQLiteDatabase db_update = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db_update.beginTransaction();
        ContentValues values = new ContentValues();
        String whereclause = "uuid=?";
        String[] selectionArgs = {visitUUID};
        try
        {
//            values.put("speciality_uuid", cursor_uuid);
            values.put("speciality_value", spinner_value);
            values.put("sync", "0");

            Logger.logD("visit", "updated_specilaity_values " +
                    values.get("speciality_value"));

            int i = db_update.update("tbl_visit", values, whereclause, selectionArgs);

            Logger.logD("visit", "updated_specilaity" + i);
            db_update.setTransactionSuccessful();
            if(i != -1)
                isupdatedone = true;

        }
        catch (SQLException e)
        {
            isupdatedone = false;
            Logger.logD("visit", "updated" + e.getMessage());
            throw new DAOException(e.getMessage());

        }
     finally {
            db_update.endTransaction();
//            db_update.close(); Closing the db was causing the crash on visit onCreate() in update.
            //while updating, do not close the db instance,.

    }

        //Sqlite Db Browser bug isnt showing the updated records...
        //To re-check and confirm that the records are updated & stored in the local db, below is
        //the code....
      */
/*  SQLiteDatabase db_aa = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db_aa.beginTransaction();
        Cursor idCursor_aa = db_aa.rawQuery("SELECT speciality_uuid, speciality_value FROM tbl_visit WHERE uuid = ?", new String[]{visitUUID});

        if(idCursor_aa.getCount() != 0)
        {
            while(idCursor_aa.moveToNext())
            {
                String aa_uuid = idCursor_aa.getString(idCursor_aa.getColumnIndexOrThrow("speciality_uuid"));
                String aa_value = idCursor_aa.getString(idCursor_aa.getColumnIndexOrThrow("speciality_value"));
                Log.d("PRAJ", "PRAJ: "+ aa_uuid + " :: " + aa_value);
            }
        }
        idCursor_aa.close();
        db_aa.setTransactionSuccessful();
        db_aa.endTransaction();*//*


        return  isupdatedone;
}
*/


    //update - end....

    public List<VisitDTO> unsyncedVisits() {
        List<VisitDTO> visitDTOList = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_visit where (sync = ? OR sync=?) COLLATE NOCASE", new String[]{"0", "false"});
        VisitDTO visitDTO = new VisitDTO();
        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {
                visitDTO = new VisitDTO();
                visitDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                visitDTO.setPatientuuid(idCursor.getString(idCursor.getColumnIndexOrThrow("patientuuid")));
                visitDTO.setLocationuuid(idCursor.getString(idCursor.getColumnIndexOrThrow("locationuuid")));
                visitDTO.setStartdate(idCursor.getString(idCursor.getColumnIndexOrThrow("startdate")));
                visitDTO.setEnddate(idCursor.getString(idCursor.getColumnIndexOrThrow("enddate")));
                visitDTO.setCreatoruuid(idCursor.getString(idCursor.getColumnIndexOrThrow("creator")));
                visitDTO.setVisitTypeUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("visit_type_uuid")));

                List<VisitAttribute_Speciality> list = new ArrayList<>();
                list = fetchVisitAttrs(visitDTO.getUuid());
                visitDTO.setAttributes(list);
//                visitDTOList.add(visitDTO);

                //adding visit attribute list in the visit data.
//               List<VisitAttribute_Speciality> list = new ArrayList<>();
//               VisitAttribute_Speciality speciality = new VisitAttribute_Speciality();
//               speciality.setAttributeType("3f296939-c6d3-4d2e-b8ca-d7f4bfd42c2d");
//               speciality.setValue(idCursor.getString(idCursor.getColumnIndexOrThrow("speciality_value")));
//               list.add(speciality);
//
//
//                visitDTO.setAttributes(list);
                //need a return value as list so that I can then add it to visitDTO.setAttributes(list);
//               list =  fetchVisitAttr_Speciality();
//               visitDTO.setAttributes(list);

                visitDTOList.add(visitDTO);
            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

//        List<VisitAttribute_Speciality> list = new ArrayList<>();
//        list = fetchVisitAttr_Speciality();
//        visitDTO.setAttributes(list);
//        visitDTOList.add(visitDTO);

        return visitDTOList;
    }

    private List<VisitAttribute_Speciality> fetchVisitAttrs(String visit_uuid) {
        List<VisitAttribute_Speciality> list = new ArrayList<>();
       // VisitAttribute_Speciality speciality = new VisitAttribute_Speciality();

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();

//        Cursor cursor = db.rawQuery("SELECT * FROM tbl_visit_attribute WHERE visit_uuid=? LIMIT 1",
//                new String[]{/*"0", */visit_uuid});

        Cursor cursor = db.rawQuery("SELECT * FROM tbl_visit_attribute WHERE visit_uuid = ?",
                new String[]{/*"0", */visit_uuid});
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                VisitAttribute_Speciality attribute = new VisitAttribute_Speciality();
                attribute.setUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
                attribute.setAttributeType(cursor.getString(cursor.getColumnIndexOrThrow("visit_attribute_type_uuid")));
                attribute.setValue(cursor.getString(cursor.getColumnIndexOrThrow("value")));
                list.add(attribute);
            }
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return list;
    }

    public List<VisitDTO> getAllVisits() {
        List<VisitDTO> visitDTOList = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWritableDatabase();
        db.beginTransaction();
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_visit", null);
        VisitDTO visitDTO = new VisitDTO();
        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {
                visitDTO = new VisitDTO();
                visitDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                visitDTO.setPatientuuid(idCursor.getString(idCursor.getColumnIndexOrThrow("patientuuid")));
                visitDTO.setLocationuuid(idCursor.getString(idCursor.getColumnIndexOrThrow("locationuuid")));
                visitDTO.setStartdate(idCursor.getString(idCursor.getColumnIndexOrThrow("startdate")));
                visitDTO.setEnddate(idCursor.getString(idCursor.getColumnIndexOrThrow("enddate")));
                visitDTO.setCreatoruuid(idCursor.getString(idCursor.getColumnIndexOrThrow("creator")));
                visitDTO.setVisitTypeUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("visit_type_uuid")));
                visitDTOList.add(visitDTO);
            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return visitDTOList;
    }

    public boolean updateVisitSync(String uuid, String synced) throws DAOException {
        boolean isUpdated = true;
        Logger.logD("visitdao", "updatesynv visit " + uuid + synced);
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        String whereclause = "uuid=?";
        String[] whereargs = {uuid};
        try {
            values.put("sync", synced);
            values.put("uuid", uuid);
            int i = db.update("tbl_visit", values, whereclause, whereargs);
            Logger.logD("visit", "updated" + i);
            db.setTransactionSuccessful();
        } catch (SQLException sql) {
            Logger.logD("visit", "updated" + sql.getMessage());
            throw new DAOException(sql.getMessage());
        } finally {
            db.endTransaction();


        }

        return isUpdated;
    }


    public boolean updateVisitEnddate(String uuid, String enddate) throws DAOException {
        boolean isUpdated = true;
        Logger.logD("visitdao", "updatesynv visit " + uuid + enddate);
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        String whereclause = "uuid=?";
        String[] whereargs = {uuid};
        try {
            values.put("enddate", enddate);
            values.put("sync", "0");
            int i = db.update("tbl_visit", values, whereclause, whereargs);
            Logger.logD("visit", "updated" + i);
            db.setTransactionSuccessful();
        } catch (SQLException sql) {
            Logger.logD("visit", "updated" + sql.getMessage());
            throw new DAOException(sql.getMessage());
        } finally {
            db.endTransaction();


        }

        return isUpdated;
    }

    public String patientUuidByViistUuid(String visituuid) {
        String patientUuidByViistUuid = "";
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT patientuuid FROM tbl_visit where uuid = ? ", new String[]{visituuid});
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                patientUuidByViistUuid = cursor.getString(cursor.getColumnIndexOrThrow("patientuuid"));
            }
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();


        return patientUuidByViistUuid;
    }

    public boolean isUpdatedDownloadColumn(String visitUuid, boolean isupdated) throws DAOException {
        boolean isUpdated = false;
        int updatedcount = 0;
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        String whereclause = "uuid=?";
        String[] whereargs = {visitUuid};
        try {
            values.put("isdownloaded", isupdated);
            updatedcount = db.update("tbl_visit", values, whereclause, whereargs);
            if (updatedcount != 0)
                isUpdated = true;
            Logger.logD("visit", "updated isdownloaded" + updatedcount);
            db.setTransactionSuccessful();
        } catch (SQLException sql) {
            isUpdated = false;
            FirebaseCrashlytics.getInstance().recordException(sql);
            Logger.logD("visit", "updated isdownloaded" + sql.getMessage());
            throw new DAOException(sql.getMessage());
        } finally {
            db.endTransaction();
        }
        return isUpdated;
    }

    public String getDownloadedValue(String visituuid) throws DAOException {
        String isDownloaded = null;

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();

        try {
            Cursor cursor = db.rawQuery("SELECT isdownloaded FROM tbl_visit where uuid = ? ", new String[]{visituuid});
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    isDownloaded = cursor.getString(cursor.getColumnIndexOrThrow("isdownloaded"));
                }
            }
            cursor.close();

        } catch (SQLiteException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            throw new DAOException(e);
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        return isDownloaded;
    }

    /**
     * Checking for the provided visitUUID if the visit is Ended or not by checking the enddate column for NULL value.
     * @param visitUUID
     * @return
     */
    public static PrescriptionModel isVisitNotEnded(String visitUUID) {
       PrescriptionModel model = new PrescriptionModel();

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT * FROM tbl_visit where uuid = ? and (sync = 1 OR sync = 'TRUE' OR sync = 'true') AND " +
                   "voided = 0 AND enddate is null", new String[]{visitUUID});  // enddate is null ie. visit is not yet ended.

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                model.setVisitUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
                model.setPatientUuid(cursor.getString(cursor.getColumnIndexOrThrow("patientuuid")));
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

       return model;
    }


    /**
     * Todays Visits that are not Ended.
     */
    public static List<PrescriptionModel> todays_NotEndedVisits() {
        List<PrescriptionModel> arrayList = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT p.uuid, v.uuid as visitUUID, p.patient_photo, p.first_name, p.last_name, v.startdate " +
                "FROM tbl_patient p, tbl_visit v WHERE p.uuid = v.patientuuid and (v.sync = 1 OR v.sync = 'TRUE' OR v.sync = 'true') AND " +
                "v.voided = 0 AND (substr(v.startdate, 1, 4) ||'-'|| substr(v.startdate, 6,2) ||'-'|| substr(v.startdate, 9,2)) = DATE('now')" +
                " AND v.enddate IS NULL", new String[]{});

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                PrescriptionModel model = new PrescriptionModel();

                model.setPatientUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
                model.setPatient_photo(cursor.getString(cursor.getColumnIndexOrThrow("patient_photo")));
                model.setVisitUuid(cursor.getString(cursor.getColumnIndexOrThrow("visitUUID")));
                model.setFirst_name(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
                model.setLast_name(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                model.setVisit_start_date(cursor.getString(cursor.getColumnIndexOrThrow("startdate")).substring(0,10));
                arrayList.add(model);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return arrayList;
    }

    /**
     * This Weeks Visits that are not Ended.
     */
    public static List<PrescriptionModel> thisWeeks_NotEndedVisits() {
        List<PrescriptionModel> arrayList = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT p.uuid, v.uuid as visitUUID, p.patient_photo, p.first_name, p.last_name, v.startdate " +
                "FROM tbl_patient p, tbl_visit v WHERE p.uuid = v.patientuuid and (v.sync = 1 OR v.sync = 'TRUE' OR v.sync = 'true') AND " +
                "v.voided = 0 AND " +
                "STRFTIME('%Y',date(substr(v.startdate, 1, 4)||'-'||substr(v.startdate, 6, 2)||'-'||substr(v.startdate, 9,2))) = STRFTIME('%Y',DATE('now')) " +
                "AND STRFTIME('%W',date(substr(v.startdate, 1, 4)||'-'||substr(v.startdate, 6, 2)||'-'||substr(v.startdate, 9,2))) = STRFTIME('%W',DATE('now')) AND " +
                "v.enddate IS NULL", new String[]{});

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                PrescriptionModel model = new PrescriptionModel();

                model.setPatientUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
                model.setPatient_photo(cursor.getString(cursor.getColumnIndexOrThrow("patient_photo")));
                model.setVisitUuid(cursor.getString(cursor.getColumnIndexOrThrow("visitUUID")));
                model.setFirst_name(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
                model.setLast_name(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                model.setVisit_start_date(cursor.getString(cursor.getColumnIndexOrThrow("startdate")).substring(0,10));
                arrayList.add(model);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return arrayList;
    }

    /**
     * This Months Visits that are not Ended.
     */
    public static List<PrescriptionModel> thisMonths_NotEndedVisits() {
        List<PrescriptionModel> arrayList = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();

        Cursor cursor = db.rawQuery("SELECT p.uuid, v.uuid as visitUUID, p.patient_photo, p.first_name, p.last_name, v.startdate " +
                "FROM tbl_patient p, tbl_visit v WHERE p.uuid = v.patientuuid and (v.sync = 1 OR v.sync = 'TRUE' OR v.sync = 'true') AND " +
                "v.voided = 0 AND " +
                "STRFTIME('%Y',date(substr(v.startdate, 1, 4)||'-'||substr(v.startdate, 6, 2)||'-'||substr(v.startdate, 9,2))) = STRFTIME('%Y',DATE('now')) AND " +
                "STRFTIME('%m',date(substr(v.startdate, 1, 4)||'-'||substr(v.startdate, 6, 2)||'-'||substr(v.startdate, 9,2))) = STRFTIME('%m',DATE('now')) AND " +
                "v.enddate IS NULL", new String[]{});

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                PrescriptionModel model = new PrescriptionModel();

                model.setPatientUuid(cursor.getString(cursor.getColumnIndexOrThrow("uuid")));
                model.setPatient_photo(cursor.getString(cursor.getColumnIndexOrThrow("patient_photo")));
                model.setVisitUuid(cursor.getString(cursor.getColumnIndexOrThrow("visitUUID")));
                model.setFirst_name(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
                model.setLast_name(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
                model.setVisit_start_date(cursor.getString(cursor.getColumnIndexOrThrow("startdate")).substring(0,10));
                arrayList.add(model);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return arrayList;
    }

    public static String fetchVisitModifiedDateForPrescPending(String visitUUID) {
        String modifiedDate = "";

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWritableDatabase();
        db.beginTransaction();

        if(visitUUID != null) {
            final Cursor cursor = db.rawQuery("select p.first_name, p.last_name, o.obsservermodifieddate from tbl_patient as p, tbl_visit as v, tbl_encounter as e, tbl_obs as o where " +
                    "p.uuid = v.patientuuid and v.uuid = e.visituuid and e.uuid = o.encounteruuid and " +
                    "(o.sync = 'TRUE' OR o.sync = 'true' OR o.sync = 1) and o.voided = 0 and " +
                    "v.uuid = ? and " +
                    "e.encounter_type_uuid = ? group by p.openmrs_id",
                    new String[]{visitUUID, ENCOUNTER_ADULTINITIAL});

            if (cursor.moveToFirst()) {
                do {
                    try {
                        modifiedDate = cursor.getString(cursor.getColumnIndexOrThrow("obsservermodifieddate"));
                        Log.v("obsservermodifieddate", "obsservermodifieddate: " + modifiedDate);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        return modifiedDate;
    }

    /**
     * This function is used to return counts of todays, thisweeks, thismonths visit who are NOT ENDED by HW.
     * @return
     */
    public static int getTotalCounts_EndVisit() {
        int total = 0;

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWritableDatabase();
        db.beginTransaction();

        // Todays cursor
        final Cursor today_cursor = db.rawQuery("SELECT count(*) FROM  tbl_visit  where (sync = 1 OR sync = 'TRUE' OR sync = 'true') AND voided = 0 AND " +
                "(substr(startdate, 1, 4) ||'-'|| substr(startdate, 6,2) ||'-'|| substr(startdate, 9,2)) = DATE('now') AND enddate IS NULL", new String[]{});
        if (today_cursor.moveToFirst()) {
            do {
                total = total + today_cursor.getInt(0);
            }
            while (today_cursor.moveToNext());
        }
            today_cursor.close();

                // Week cursor
        final Cursor week_cursor = db.rawQuery("SELECT count(*) FROM  tbl_visit  where (sync = 1 OR sync = 'TRUE' OR sync = 'true') AND voided = 0 AND " +
                "STRFTIME('%Y',date(substr(startdate, 1, 4)||'-'||substr(startdate, 6, 2)||'-'||substr(startdate, 9,2))) = STRFTIME('%Y',DATE('now')) " +
                "AND STRFTIME('%W',date(substr(startdate, 1, 4)||'-'||substr(startdate, 6, 2)||'-'||substr(startdate, 9,2))) = STRFTIME('%W',DATE('now')) AND enddate IS NULL", new String[]{});
        if (week_cursor.moveToFirst()) {
            do {
                total = total + week_cursor.getInt(0);
            }
            while (week_cursor.moveToNext());
        }
            week_cursor.close();

            // Month cursor
        final Cursor month_cursor = db.rawQuery("SELECT count(*) FROM  tbl_visit  where (sync = 1 OR sync = 'TRUE' OR sync = 'true') AND voided = 0 AND " +
                "STRFTIME('%Y',date(substr(startdate, 1, 4)||'-'||substr(startdate, 6, 2)||'-'||substr(startdate, 9,2))) = STRFTIME('%Y',DATE('now')) " +
                "AND STRFTIME('%m',date(substr(startdate, 1, 4)||'-'||substr(startdate, 6, 2)||'-'||substr(startdate, 9,2))) = STRFTIME('%m',DATE('now')) AND enddate IS NULL", new String[]{});
        if (month_cursor.moveToFirst()) {
            do {
                total = total + month_cursor.getInt(0);
            }
            while (month_cursor.moveToNext());
        }
            month_cursor.close();

        db.setTransactionSuccessful();
        db.endTransaction();

            Log.v("totalCount", "totalCountsEndVisit: " + total);

        return total;
    }



}