/*
 * This file is generated by jOOQ.
 */
package eu.dissco.core.handlemanager.database.jooq;


import eu.dissco.core.handlemanager.database.jooq.tables.Handles;
import eu.dissco.core.handlemanager.database.jooq.tables.records.HandlesRecord;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in 
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<HandlesRecord> HANDLES_PKEY = Internal.createUniqueKey(Handles.HANDLES, DSL.name("handles_pkey"), new TableField[] { Handles.HANDLES.HANDLE, Handles.HANDLES.IDX }, true);
}
