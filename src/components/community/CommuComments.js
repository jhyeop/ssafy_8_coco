import { useState,  } from "react"
import { useLocation } from "react-router-dom"
import { commentDelete, commentUpdate } from "../../api/community"

import { Card, Button, CardContent, Typography, Grid, ButtonGroup, TextField } from "@mui/material"

function Comments(props) {
    const location = useLocation()
    const [ updateFlag, setUpdateFlag ] = useState(false)
    const [ updateComment, setUpdateComment ] = useState("")
    const [ updateTarget, setUpdateTarget ] = useState("")

    async function onDeleteClick(params) {
        console.log(params)
        const commentInfo = {
            pk: params.id,
            board_id:location.state.id
        }
        await commentDelete(
            commentInfo,
            (data) => console.log(data),
            (err) => console.log(err)
        )
    }

    const onTypingHandler = (e) => {
        setUpdateComment(e.target.value)  
    }

    async function onUpdateClick(params) {
        console.log(props)
        const commentInfo = {
            pk: params.id,
            content: updateComment,
            board_id:location.state.id
        }
        await commentUpdate(
            commentInfo,
            (data) => {
                console.log(data)
                setUpdateFlag(false)
                setUpdateTarget("")
                props.isRenderNeeded()
                alert("수정 완료!")
            },
            (err) => console.log(err)
        )
    }

    const flagClickHandler = (params) => {
        if (updateFlag) {
            onUpdateClick(params)

        }  else {
            setUpdateComment(params.content)
            setUpdateTarget(params.id)
            setUpdateFlag(true)
        }
    }


    return (
        <>{props.comments.content.map(comment => {
            return (
                <Card sx={{ width: '100%', height: 'auto', margin: '4px'}} key={comment.id}>
                    <CardContent>
                        <Grid container>
                            <Grid item xs={2} textAlign="center">
                                <Typography>{comment.writer}</Typography>
                            </Grid>
                            <Grid item xs={7}>
                                {updateTarget !== comment.id ? <Typography>{comment.content}</Typography> : <TextField onChange={onTypingHandler} value={updateComment} size="small"/>}
                            </Grid>
                            <Grid item xs={1} textAlign="center">
                                {/* 오늘 /  이번 년 / 그 외 */}
                                <Typography>{comment.createdAt.slice(5, 10)}</Typography>
                            </Grid>
                            <Grid item xs={2} textAlign="center">
                                {localStorage.getItem("userId") === comment.writer
                                ?   <ButtonGroup>
                                        {updateFlag === false ? <Button onClick={() => flagClickHandler(comment)}>수정</Button> : <Button variant="contained" onClick={() => {setUpdateFlag(false); setUpdateTarget("");}}>취소</Button>}
                                        {updateFlag === false ? <Button onClick={() => onDeleteClick(comment)}>삭제</Button> : <Button variant="contained" onClick={() => flagClickHandler(comment)}>완료</Button> }
                                    </ButtonGroup>
                                : <></>
                                }
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>
                )
        })}</>
    )
}

export default Comments