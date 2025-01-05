package friendy.community.domain.auth.fixtures;

public class TokenFixtures {

    public static final String MALFORMED_JWT_TOKEN = "aabbcc";

    public static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImV4YW1wbGVAZnJpZW5keS5jb20iLCJpYXQiOjE2MDAwMDAwMDAsImV4cCI6MTYwMDAwMDEwMH0.mqOL2LPVIqTlrjqAWElM5XsJMgTjxWsEpOkr0atIdKs";

    /**
     * MISSING_CLAIM_TOKEN : 만료 기한 - 2123년 10월 30일
     */
    public static final String MISSING_CLAIM_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE2MDAwMDAwMDAsImV4cCI6NDg1NDI3ODQwMH0.65lNI_07FgETBnasvHCzxc1RDfLSoDBJr0Vebocu2gI";

}

