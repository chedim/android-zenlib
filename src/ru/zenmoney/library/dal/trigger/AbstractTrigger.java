package ru.zenmoney.library.dal.trigger;

import android.content.ContentValues;
import ru.zenmoney.library.dal.model.AbstractModel;

public abstract class AbstractTrigger {
    public void onBeforeInsert(ContentValues row, boolean writeChanged) throws Exception {
    }

    public void onInsertFail(ContentValues row, Throwable error) throws Exception  {
    }

    public void onAfterInsert(ContentValues row) throws Exception  {
    }

    public void onBeforeUpdate(ContentValues oldRow, ContentValues newRow, boolean writeChanged) throws Exception  {
    }

    public void onUpdateFail(ContentValues oldRow, ContentValues newRow, Throwable error) throws Exception  {
    }

    public void onAfterUpdate(ContentValues oldRow, ContentValues newRow) throws Exception  {
    }

    public void onBeforeDelete(ContentValues row, boolean writeChanged) throws Exception {
    }

    public void onDeleteFail(ContentValues row, Throwable error) throws Exception  {
    }

    public void onAfterDelete(ContentValues row) throws Exception  {
    }

    public boolean onExport(AbstractModel model) throws Exception{
        return true;
    }

    public boolean onImport(AbstractModel model) throws Exception {
        return true;
    }
}
