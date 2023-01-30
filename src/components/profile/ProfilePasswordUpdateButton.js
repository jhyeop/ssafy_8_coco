import { useEffect, useState } from 'react'
import { Button, Modal, Box, Typography, TextField, Grid } from '@mui/material'

function ProfilePasswordUpdateButton(params) {
    const [inputPassword, setInputPassword] = useState()
    const [inputUpdatedPassword, setInputUpdatedPassword] = useState()
    const [inputUpdatedCheckPassword, setInputUpdatedCheckPassword] = useState()
    
    const [isOkToSubmit, setIsOkToSubmit] = useState(false)
    const [isPasswordValid, setIsPasswordValid] = useState({isVaild: false})

    const [modalOpen, setModalOpen] = useState(false)
    const handleOpen = () => setModalOpen(true);
    const handleClose = () => setModalOpen(false);

    const onTypingHandler = (e) => {
        // 4개의 케이스에 따라 각자의 스테이트에 저장
        switch (e.target.id) {
            case 'password':
                setInputPassword(e.target.value)
                break
            case 'updated-password':
                setInputUpdatedPassword(e.target.value)
                break
            case 'updated-password-check':
                setInputUpdatedCheckPassword(e.target.value)
                break
            default:
                // nothing
        }
    }

    function passwordValidation() {
        const passwordForm = /^[a-z0-9]{4,12}$/
        const passwordErrorMessage = {
            null: "필수 입력입니다.",
            form: "비밀번호가 취약합니다.",
            same: "비밀번호가 일치하지 않습니다.",
        }
        if (inputUpdatedPassword === undefined || inputUpdatedPassword === '') {
            return {isVaild: true, message: passwordErrorMessage.null}
        } else if (inputUpdatedPassword !== inputUpdatedCheckPassword) {
            return {isVaild: true, message: passwordErrorMessage.same}
        } else if (!passwordForm.test(inputUpdatedPassword)) {
            return {isVaild: true, message: passwordErrorMessage.form}
        }
        else {
            return {isValid: false}
        }
    }

    useEffect(() => {
        setIsOkToSubmit(passwordValidation())
    }, [inputPassword, inputUpdatedPassword, inputUpdatedCheckPassword])

    async function updatePassword() {
        console.log('들어간다')
        
        // user_id의 입출력에 관한 코드 정리가 필요함 > store를 활용해야함
        const response = await fetch(`/member/info/${params.user.user_id}`, {
            method: 'PUT',
            body: JSON.stringify({
                password: {inputPassword}
            }),
            headers: {
                "Content-Type": `application/json`,
            }
        })
        const data = await response.json()
        console.log('들어옴', data)
    } 

    const passwordChangeHandler = () => {
        if (isOkToSubmit) {updatePassword()}
    }

    return (
        <div>
            <Button onClick={handleOpen} variant="contained" className="submit" fullWidth style={{ width: "8rem", height:"2.8rem", marginRight: "15px", backgroundColor: "white", color: "red", border: "solid 2px red"}}><b>비밀번호 변경</b></Button>
            <Modal
            open={modalOpen}
            onClose={handleClose}
            aria-labelledby="modal-modal-title"
            aria-describedby="modal-modal-description"
            >
                <Box sx={style}>
                    <Typography id="modal-modal-title" variant="h6" component="h2">
                        This is a modal
                    </Typography>
                    <Typography id="modal-modal-description" sx={{ mt: 2 }}>
                        비밀번호 변경 모달입니다.
                    </Typography>
                    <Grid container spacing={2} style={{padding: '2rem', justifyContent: 'center'}}>
                        <Grid item xs={10}>
                            <TextField onChange={onTypingHandler} id="password" label="Current Password" fullWidth/>
                        </Grid>
                        <Grid item xs={10}>
                            <TextField onChange={onTypingHandler} error={isPasswordValid.isVaild} helperText={isPasswordValid.isVaild ? isPasswordValid.message : ""}id="updated-password" type="password" label="Password" fullWidth/>
                        </Grid>
                        <Grid item xs={10}>
                            <TextField onChange={onTypingHandler} error={isPasswordValid.isVaild} helperText={isPasswordValid.isVaild ? isPasswordValid.message : ""} id="updated-password-check" type="password" label="Password Check" fullWidth/>
                        </Grid>
                        <Grid item xs={6}>
                            <Button onClick={passwordChangeHandler} variant="contained" className="submit" fullWidth style={{ width: "8rem", height:"2.8rem", marginRight: "15px", backgroundColor: "white", color: "red", border: "solid 2px red"}}><b>비밀번호 변경</b></Button>
                        </Grid>
                    </Grid>
                </Box>
            </Modal>
        </div>
    )
    
}

const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 400,
    bgcolor: 'background.paper',
    border: '2px solid #000',
    boxShadow: 24,
    p: 4,
  };

export default ProfilePasswordUpdateButton