package com.zhidejiaoyu.common.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameScoreExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public GameScoreExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andStudentIdIsNull() {
            addCriterion("student_id is null");
            return (Criteria) this;
        }

        public Criteria andStudentIdIsNotNull() {
            addCriterion("student_id is not null");
            return (Criteria) this;
        }

        public Criteria andStudentIdEqualTo(Long value) {
            addCriterion("student_id =", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotEqualTo(Long value) {
            addCriterion("student_id <>", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThan(Long value) {
            addCriterion("student_id >", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("student_id >=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThan(Long value) {
            addCriterion("student_id <", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThanOrEqualTo(Long value) {
            addCriterion("student_id <=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdIn(List<Long> values) {
            addCriterion("student_id in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotIn(List<Long> values) {
            addCriterion("student_id not in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdBetween(Long value1, Long value2) {
            addCriterion("student_id between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotBetween(Long value1, Long value2) {
            addCriterion("student_id not between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andGameIdIsNull() {
            addCriterion("game_id is null");
            return (Criteria) this;
        }

        public Criteria andGameIdIsNotNull() {
            addCriterion("game_id is not null");
            return (Criteria) this;
        }

        public Criteria andGameIdEqualTo(Long value) {
            addCriterion("game_id =", value, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameIdNotEqualTo(Long value) {
            addCriterion("game_id <>", value, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameIdGreaterThan(Long value) {
            addCriterion("game_id >", value, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameIdGreaterThanOrEqualTo(Long value) {
            addCriterion("game_id >=", value, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameIdLessThan(Long value) {
            addCriterion("game_id <", value, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameIdLessThanOrEqualTo(Long value) {
            addCriterion("game_id <=", value, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameIdIn(List<Long> values) {
            addCriterion("game_id in", values, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameIdNotIn(List<Long> values) {
            addCriterion("game_id not in", values, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameIdBetween(Long value1, Long value2) {
            addCriterion("game_id between", value1, value2, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameIdNotBetween(Long value1, Long value2) {
            addCriterion("game_id not between", value1, value2, "gameId");
            return (Criteria) this;
        }

        public Criteria andGameNameIsNull() {
            addCriterion("game_name is null");
            return (Criteria) this;
        }

        public Criteria andGameNameIsNotNull() {
            addCriterion("game_name is not null");
            return (Criteria) this;
        }

        public Criteria andGameNameEqualTo(String value) {
            addCriterion("game_name =", value, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameNotEqualTo(String value) {
            addCriterion("game_name <>", value, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameGreaterThan(String value) {
            addCriterion("game_name >", value, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameGreaterThanOrEqualTo(String value) {
            addCriterion("game_name >=", value, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameLessThan(String value) {
            addCriterion("game_name <", value, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameLessThanOrEqualTo(String value) {
            addCriterion("game_name <=", value, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameLike(String value) {
            addCriterion("game_name like", value, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameNotLike(String value) {
            addCriterion("game_name not like", value, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameIn(List<String> values) {
            addCriterion("game_name in", values, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameNotIn(List<String> values) {
            addCriterion("game_name not in", values, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameBetween(String value1, String value2) {
            addCriterion("game_name between", value1, value2, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameNameNotBetween(String value1, String value2) {
            addCriterion("game_name not between", value1, value2, "gameName");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeIsNull() {
            addCriterion("game_start_time is null");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeIsNotNull() {
            addCriterion("game_start_time is not null");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeEqualTo(Date value) {
            addCriterion("game_start_time =", value, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeNotEqualTo(Date value) {
            addCriterion("game_start_time <>", value, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeGreaterThan(Date value) {
            addCriterion("game_start_time >", value, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("game_start_time >=", value, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeLessThan(Date value) {
            addCriterion("game_start_time <", value, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeLessThanOrEqualTo(Date value) {
            addCriterion("game_start_time <=", value, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeIn(List<Date> values) {
            addCriterion("game_start_time in", values, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeNotIn(List<Date> values) {
            addCriterion("game_start_time not in", values, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeBetween(Date value1, Date value2) {
            addCriterion("game_start_time between", value1, value2, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameStartTimeNotBetween(Date value1, Date value2) {
            addCriterion("game_start_time not between", value1, value2, "gameStartTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeIsNull() {
            addCriterion("game_end_time is null");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeIsNotNull() {
            addCriterion("game_end_time is not null");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeEqualTo(Date value) {
            addCriterion("game_end_time =", value, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeNotEqualTo(Date value) {
            addCriterion("game_end_time <>", value, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeGreaterThan(Date value) {
            addCriterion("game_end_time >", value, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("game_end_time >=", value, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeLessThan(Date value) {
            addCriterion("game_end_time <", value, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeLessThanOrEqualTo(Date value) {
            addCriterion("game_end_time <=", value, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeIn(List<Date> values) {
            addCriterion("game_end_time in", values, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeNotIn(List<Date> values) {
            addCriterion("game_end_time not in", values, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeBetween(Date value1, Date value2) {
            addCriterion("game_end_time between", value1, value2, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andGameEndTimeNotBetween(Date value1, Date value2) {
            addCriterion("game_end_time not between", value1, value2, "gameEndTime");
            return (Criteria) this;
        }

        public Criteria andScoreIsNull() {
            addCriterion("score is null");
            return (Criteria) this;
        }

        public Criteria andScoreIsNotNull() {
            addCriterion("score is not null");
            return (Criteria) this;
        }

        public Criteria andScoreEqualTo(Integer value) {
            addCriterion("score =", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotEqualTo(Integer value) {
            addCriterion("score <>", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreGreaterThan(Integer value) {
            addCriterion("score >", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreGreaterThanOrEqualTo(Integer value) {
            addCriterion("score >=", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLessThan(Integer value) {
            addCriterion("score <", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreLessThanOrEqualTo(Integer value) {
            addCriterion("score <=", value, "score");
            return (Criteria) this;
        }

        public Criteria andScoreIn(List<Integer> values) {
            addCriterion("score in", values, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotIn(List<Integer> values) {
            addCriterion("score not in", values, "score");
            return (Criteria) this;
        }

        public Criteria andScoreBetween(Integer value1, Integer value2) {
            addCriterion("score between", value1, value2, "score");
            return (Criteria) this;
        }

        public Criteria andScoreNotBetween(Integer value1, Integer value2) {
            addCriterion("score not between", value1, value2, "score");
            return (Criteria) this;
        }

        public Criteria andAwardGoldIsNull() {
            addCriterion("award_gold is null");
            return (Criteria) this;
        }

        public Criteria andAwardGoldIsNotNull() {
            addCriterion("award_gold is not null");
            return (Criteria) this;
        }

        public Criteria andAwardGoldEqualTo(Integer value) {
            addCriterion("award_gold =", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldNotEqualTo(Integer value) {
            addCriterion("award_gold <>", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldGreaterThan(Integer value) {
            addCriterion("award_gold >", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldGreaterThanOrEqualTo(Integer value) {
            addCriterion("award_gold >=", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldLessThan(Integer value) {
            addCriterion("award_gold <", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldLessThanOrEqualTo(Integer value) {
            addCriterion("award_gold <=", value, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldIn(List<Integer> values) {
            addCriterion("award_gold in", values, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldNotIn(List<Integer> values) {
            addCriterion("award_gold not in", values, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldBetween(Integer value1, Integer value2) {
            addCriterion("award_gold between", value1, value2, "awardGold");
            return (Criteria) this;
        }

        public Criteria andAwardGoldNotBetween(Integer value1, Integer value2) {
            addCriterion("award_gold not between", value1, value2, "awardGold");
            return (Criteria) this;
        }

        public Criteria andPassFlagIsNull() {
            addCriterion("pass_flag is null");
            return (Criteria) this;
        }

        public Criteria andPassFlagIsNotNull() {
            addCriterion("pass_flag is not null");
            return (Criteria) this;
        }

        public Criteria andPassFlagEqualTo(Integer value) {
            addCriterion("pass_flag =", value, "passFlag");
            return (Criteria) this;
        }

        public Criteria andPassFlagNotEqualTo(Integer value) {
            addCriterion("pass_flag <>", value, "passFlag");
            return (Criteria) this;
        }

        public Criteria andPassFlagGreaterThan(Integer value) {
            addCriterion("pass_flag >", value, "passFlag");
            return (Criteria) this;
        }

        public Criteria andPassFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("pass_flag >=", value, "passFlag");
            return (Criteria) this;
        }

        public Criteria andPassFlagLessThan(Integer value) {
            addCriterion("pass_flag <", value, "passFlag");
            return (Criteria) this;
        }

        public Criteria andPassFlagLessThanOrEqualTo(Integer value) {
            addCriterion("pass_flag <=", value, "passFlag");
            return (Criteria) this;
        }

        public Criteria andPassFlagIn(List<Integer> values) {
            addCriterion("pass_flag in", values, "passFlag");
            return (Criteria) this;
        }

        public Criteria andPassFlagNotIn(List<Integer> values) {
            addCriterion("pass_flag not in", values, "passFlag");
            return (Criteria) this;
        }

        public Criteria andPassFlagBetween(Integer value1, Integer value2) {
            addCriterion("pass_flag between", value1, value2, "passFlag");
            return (Criteria) this;
        }

        public Criteria andPassFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("pass_flag not between", value1, value2, "passFlag");
            return (Criteria) this;
        }

        public Criteria andExplainIsNull() {
            addCriterion("explain is null");
            return (Criteria) this;
        }

        public Criteria andExplainIsNotNull() {
            addCriterion("explain is not null");
            return (Criteria) this;
        }

        public Criteria andExplainEqualTo(String value) {
            addCriterion("explain =", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotEqualTo(String value) {
            addCriterion("explain <>", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainGreaterThan(String value) {
            addCriterion("explain >", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainGreaterThanOrEqualTo(String value) {
            addCriterion("explain >=", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLessThan(String value) {
            addCriterion("explain <", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLessThanOrEqualTo(String value) {
            addCriterion("explain <=", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainLike(String value) {
            addCriterion("explain like", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotLike(String value) {
            addCriterion("explain not like", value, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainIn(List<String> values) {
            addCriterion("explain in", values, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotIn(List<String> values) {
            addCriterion("explain not in", values, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainBetween(String value1, String value2) {
            addCriterion("explain between", value1, value2, "explain");
            return (Criteria) this;
        }

        public Criteria andExplainNotBetween(String value1, String value2) {
            addCriterion("explain not between", value1, value2, "explain");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}