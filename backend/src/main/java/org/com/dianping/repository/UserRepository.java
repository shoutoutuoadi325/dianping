package org.com.dianping.repository;

import org.com.dianping.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

//数据库调用方法save等早已被继承
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    // Repository层使用方法介绍
    // 在Spring Data JPA的Repository层中，方法可以分为以下三类：
    // 1. 无需编写的（Spring Data JPA 自动提供）
    // Spring Data JPA 继承自 JpaRepository 的基本CRUD方法，直接可用，无需额外编写。例如：
    // ● save(Entity entity): 保存或更新实体
    // ● deleteById(ID id): 根据主键删除记录
    // ● findById(ID id): 根据主键查找记录
    // ● findAll(): 查询所有记录
    // 2. 可以通过命名提供的（Spring Data JPA 解析方法名自动生成查询）
    // Spring Data JPA 支持通过方法名称的约定来自动生成SQL查询，方法名称以特定的格式开始，系统会自动解析并执行相应的查询。常见的命名方式有：
    // ● findBy...：根据指定字段查询数据。例如findByName(String name)：根据name字段查找所有符合条件的用户。
    // ● existsBy...：判断某个字段是否存在满足条件的记录。例如existsByEmail(String
    // email)：检查数据库中是否存在指定邮箱的用户，返回布尔值。
    // ● countBy...：统计满足条件的记录数量。例如countByStatus(String status)：统计指定status的用户数量。
    // 3. 需要自行编写SQL查询语句的（使用@Query）
    // 当查询逻辑较复杂，无法通过方法命名自动生成SQL时，需要使用@Query手写SQL，例如：
    // @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword%")
    // List<User> searchByName(@Param("keyword") String keyword);
    // 关于Repository的常见Q&A
    // ℹ️ 提示
    // 1. 为什么我没有在Repository接口中声明save()、findById()等方法，我却可以直接使用？
    // 因为UserRepository继承了JpaRepository，而JpaRepository已经为你提供了这些方法。
    // 2. 为什么我没有提供Repository接口的实现，我却可以直接使用？
    // Spring Data JPA通过动态代理机制自动为你生成Repository的实现类。你只需定义接口，Spring Boot会在运行时自动处理实现。
    // 3. JpaRepository只提供了findById()方法，如果我想要按其他条件查找，例如findByName()，我该怎么做？
    // 可以通过在Repository接口中定义类似findByName(String name)的方法，Spring Data
    // JPA会根据方法名称自动生成相应的查询语句。
    // 4. 我想要使用更复杂的SQL语句查询，我该怎么做？
    // 你可以在Repository中使用@Query注解，手动编写SQL查询。例如：@Query("SELECT u FROM User u WHERE
    // u.name = :name")。
}
