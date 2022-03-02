package sogong.sogongSpring.entity

import javax.persistence.*

@Entity
@Table(name = "BASIC_LOGIN")
class BasicLoginEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val basicid : Int,

    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    private val userid : UserLoginEntity,

    @Column(nullable = false, length = 15)
    private val realid : String,

    @Column(nullable = false, length = 20)
    private var passwd : String
)