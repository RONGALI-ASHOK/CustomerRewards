package com.charter.rewardpoints.validator;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.charter.rewardpoints.exception.RewardPointsException;
import com.charter.rewardpoints.utility.RewardValidator;

@DisplayName("RewardPointsValidator Tests")
@SuppressWarnings({ "unused", "ThrowableResultIgnored" })
class RewardPointsValidatorTest {

        private RewardValidator validator;
        @BeforeEach
        void setUp() {
                validator = new RewardValidator();
        }

        @Test
        void testBothMonthsAndDatesProvided() {
                RewardPointsException ex = assertThrows(RewardPointsException.class, () ->
                validator.validateInputParameters(
                                2,
                                LocalDate.now().minusMonths(1),
                                LocalDate.now()
                ));
                assertNotNull(ex.getMessage());
        }

        @Test
        void testOnlyFromDateProvided() {

                RewardPointsException ex = assertThrows(RewardPointsException.class, () ->
                validator.validateInputParameters(
                                null,
                                LocalDate.now().minusMonths(1),
                                null

                ));
                assertNotNull(ex.getMessage());
        }

        @Test
        void testOnlyToDateProvided() {

                RewardPointsException ex = assertThrows(RewardPointsException.class, () ->
                validator.validateInputParameters(
                                null,
                                null,
                                LocalDate.now()
                ));
                assertNotNull(ex.getMessage());
        }

        @Test
        void testValidNoOfMonthsOnly() {
                assertDoesNotThrow(() ->
                validator.validateInputParameters(3, null, null));
        }

        @Test
        void testValidDateRangeInput() {

                assertDoesNotThrow(() ->
                validator.validateInputParameters(
                                null,
                                LocalDate.now().minusMonths(2),
                                LocalDate.now()
                ));
        }

        @Test
        void testFromDateAfterToDate() {

                RewardPointsException ex = assertThrows(RewardPointsException.class, () ->
                validator.validateDateRange(
                                LocalDate.now(),
                                LocalDate.now().minusDays(1)
                ));
                assertEquals("The toDate must be after the fromDate.", ex.getMessage());
        }

        @Test
        void testValidDateRange() {

                assertDoesNotThrow(() ->
                validator.validateDateRange(
                                LocalDate.now().minusMonths(1),
                                LocalDate.now()
                ));
        }

        @Test
        void testDetermineRangeWithMonths() {

                LocalDate[] result = validator.determineEffectiveDateRange(2, null, null);
                assertNotNull(result);
                assertEquals(LocalDate.now(), result[1]);
                assertEquals(LocalDate.now().minusMonths(2), result[0]);

        }

        @Test
        void testDetermineRangeWithDates() {

                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 2, 1);
                LocalDate[] result = validator.determineEffectiveDateRange(null, from, to);
                assertEquals(from, result[0]);
                assertEquals(to, result[1]);

        }

        @Test
        void testDefaultMonthsWhenAllNull() {

                LocalDate[] result = validator.determineEffectiveDateRange(null, null, null);
                assertNotNull(result);
                assertEquals(LocalDate.now(), result[1]);
                assertEquals(LocalDate.now().minusMonths(3), result[0]);
        }
}
