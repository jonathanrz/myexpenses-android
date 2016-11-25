package br.com.jonathanzanella.myexpenses.database

import br.com.jonathanzanella.myexpenses.Environment
import com.raizlabs.android.dbflow.annotation.Database

/**
 * Created by jonathan on 01/11/15.
 * Copyright (c) 2015. All rights reserved.
 */
@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
object MyDatabase {
    const val NAME = Environment.DB_NAME

    const val VERSION = Environment.DB_VERSION
}