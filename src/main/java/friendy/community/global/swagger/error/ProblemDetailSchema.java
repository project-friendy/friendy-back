package friendy.community.global.swagger.error;

public record ProblemDetailSchema(
        String type,
        String title,
        int status,
        String detail,
        String instance
) {
    public static ProblemDetailSchema of(ApiErrorResponse apiErrorResponse, String detail) {
        return new ProblemDetailSchema(
                apiErrorResponse.type(),
                apiErrorResponse.status().name(),
                apiErrorResponse.status().value(),
                detail,
                apiErrorResponse.instance()
        );
    }
}
