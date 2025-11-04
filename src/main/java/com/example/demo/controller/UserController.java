@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;  // Mapper handles conversion
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        // DTO provides validation and structure
        // Mapper converts DTO → Entity → DTO
        User user = userMapper.toEntity(createDTO);
        User savedUser = userService.save(user);
        UserDTO responseDTO = userMapper.toDTO(savedUser);
        return ResponseEntity.ok(responseDTO);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDTO userDTO = userMapper.toDTO(user);  // Mapper converts Entity → DTO
        return ResponseEntity.ok(userDTO);
    }
}
