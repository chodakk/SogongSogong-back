package sogong.sogongSpring.repository

import org.springframework.data.jpa.repository.JpaRepository
import sogong.sogongSpring.entity.BasicLoginEntity

interface BasicLoginRepository : JpaRepository<BasicLoginEntity, Int> {
}