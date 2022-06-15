public class SomeUserGodObject {
   private static final String FIND_ALL_USERS_EN = "SELECT id, email, phone, first_name_en, access_counter, middle_name_en, last_name_en, created_date FROM users;
   private static final String FIND_BY_ID = "SELECT id, email, phone, first_name_en, access_counter, middle_name_en, last_name_en, created_date FROM users WHERE id = ?";
   private static final String FIND_ALL_CUSTOMERS = "SELECT id, u.email, u.phone, u.first_name_en, u.middle_name_en, u.last_name_en, u.created_date" +
           "  WHERE u.id IN (SELECT up.user_id FROM user_permissions up WHERE up.permission_id = ?)";
   private static final String FIND_BY_EMAIL = "SELECT id, email, phone, first_name_en, access_counter, middle_name_en, last_name_en, created_dateFROM users WHERE email = ?";
   private static final String LIMIT_OFFSET = " LIMIT ? OFFSET ?";
   private static final String ORDER = " ORDER BY ISNULL(last_name_en), last_name_en, ISNULL(first_name_en), first_name_en, ISNULL(last_name_ru), " +
           "last_name_ru, ISNULL(first_name_ru), first_name_ru";
   private static final String CREATE_USER_EN = "INSERT INTO users(id, phone, email, first_name_en, middle_name_en, last_name_en, created_date) " +
           "VALUES (?, ?, ?, ?, ?, ?, ?)";
   private static final String FIND_ID_BY_LANG_CODE = "SELECT id FROM languages WHERE lang_code = ?";
                                  ........
   private final JdbcTemplate jdbcTemplate;
   private Map<String, String> firstName;
   private Map<String, String> middleName;
   private Map<String, String> lastName;
   private List<Long> permission;
                                   ........
   @Override
   public List<User> findAllEnCustomers(Long permissionId) {
       return jdbcTemplate.query( FIND_ALL_CUSTOMERS + ORDER, userRowMapper(), permissionId);
   }
   @Override
   public List<User> findAllEn() {
       return jdbcTemplate.query(FIND_ALL_USERS_EN + ORDER, userRowMapper());
   }
   @Override
   public Optional<List<User>> findAllEnByEmail(String email) {
       var query = FIND_ALL_USERS_EN + FIND_BY_EMAIL + ORDER;
       return Optional.ofNullable(jdbcTemplate.query(query, userRowMapper(), email));
   }
                              .............
   private List<User> findAllWithoutPageEn(Long permissionId, Type type) {
       switch (type) {
           case USERS:
               return findAllEnUsers(permissionId);
           case CUSTOMERS:
               return findAllEnCustomers(permissionId);
           default:
               return findAllEn();
       }
   }
                              ..............…

   private RowMapper<User> userRowMapperEn() {
       return (rs, rowNum) ->
               User.builder()
                       .id(rs.getLong("id"))
                       .email(rs.getString("email"))
                       .accessFailed(rs.getInt("access_counter"))
                       .createdDate(rs.getObject("created_date", LocalDateTime.class))
                       .firstName(rs.getString("first_name_en"))
                       .middleName(rs.getString("middle_name_en"))
                       .lastName(rs.getString("last_name_en"))
                       .phone(rs.getString("phone"))
                       .build();
   }
}
