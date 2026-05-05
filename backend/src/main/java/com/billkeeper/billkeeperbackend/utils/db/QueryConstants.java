package com.billkeeper.billkeeperbackend.utils.db;

public class QueryConstants {

    public static final String USER_OR_FAMILY_MEMBER_FILTER = """
            (b.user.id = :#{#user.id} OR (:#{#user.family?.id} IS NOT NULL AND b.user.family IS NOT NULL AND b.user.family.id = :#{#user.family?.id}))
            """;
}