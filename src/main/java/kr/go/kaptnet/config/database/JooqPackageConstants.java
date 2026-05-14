package kr.go.kaptnet.config.database;

/**
 * JOOQ 생성 코드 패키지 경로 상수
 *
 * 이 클래스는 JOOQ에서 생성된 코드의 패키지 경로를 중앙에서 관리합니다.
 * MyBatis Type Aliases, import 문 등에서 사용되며,
 * 이를 통해 패키지 이름 변경 시 모든 참조를 일관되게 업데이트할 수 있습니다.
 */
public class JooqPackageConstants {

	/**
	 * JOOQ 생성 코드의 기본 패키지명
	 */
	public static final String JOOQ_GENERATED_BASE = "kr.go.kaptnet.jooq.generated";

	/**
	 * JOOQ 생성 POJO 클래스의 패키지명
	 * 예: UserEntity, ProductEntity 등
	 */
	public static final String JOOQ_POJO_PACKAGE = JOOQ_GENERATED_BASE + ".tables.pojos";

	/**
	 * DTO 클래스의 패키지명
	 */
	public static final String DTO_PACKAGE = "kr.go.kaptnet.dto";

	/**
	 * MyBatis Type Aliases 패키지 설정
	 * MyBatis 설정에서 직접 사용할 수 있는 형식
	 */
	public static final String MYBATIS_TYPE_ALIASES_PACKAGE = JOOQ_POJO_PACKAGE + ".**," + DTO_PACKAGE + ".**";
}
