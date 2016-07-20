Check connect.php for the tables' names and columns

Users ("users")
id | nick | avatar | birthDay | description | gender | isTracked
-- | ---- | ------ | -------- | ----------- | ------ | ---------
INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE | VARCHAR NOT NULL | BLOB NOT NULL | DATE NOT NULL | VARCHAR NOT NULL | INTEGER NOT NULL | BOOL  NOT NULL DEFAULT 1

Global chat ("chat")
id | time | nick | isImage | msg
-- | ---- | ---- | ------- | ---
INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE | DATETIME NOT NULL | VARCHAR NOT NULL |  BOOL NOT NULL DEFAULT 0 | BLOB NOT NULL

Private chats ("privates")
id | id_sender | id_receiver | time | isImage | msg
-- | --------- | ----------- | ---- | ------- | ---
INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE | INTEGER NOT NULL | INTEGER NOT NULL | DATETIME NOT NULL | BOOL NOT NULL  DEFAULT 0 | BLOB NOT NULL 

<<<<<<< HEAD
Updated Serverside Files 2016-07-06
=======
Updated Serverside Files 2016-07-18 @RoadRunner
=======
>>>>>>> 08f80df25fa787b97f8132548cc724b8a0651ca3
>>>>>>> e7909abdd87f6b482e287ca3b7e1ec86660a9065
