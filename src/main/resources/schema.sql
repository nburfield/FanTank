create table IF NOT EXISTS UserConnection (userId varchar(255) not null,
    providerId varchar(255) not null,
    providerUserId varchar(255),
    rank int not null,
    displayName varchar(255),
    profileUrl varchar(512),
    imageUrl varchar(512),
    accessToken varchar(512) not null,
    secret varchar(512),
    refreshToken varchar(512),
    expireTime bigint,
    primary key (userId, providerId, providerUserId));

set @x := (SELECT count(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME='UserConnection' AND INDEX_NAME='UserConnectionRank');
set @sql := if( @x = 3, 'select ''Index exists.''', 'create unique index UserConnectionRank on UserConnection(userId, providerId, rank);');
PREPARE stmt FROM @sql;
EXECUTE stmt;
