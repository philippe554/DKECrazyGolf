__kernel void ballSide(__global const float *points,__global const int *sides, __global const float *sidesData,__global const float *ball,__global char *colDetected)
{
    int gid = get_global_id(0);
    int lid = get_global_id(1);
    //colDetected[gid]=colDetected[gid]&(~(1<<lid));
    char CD=colDetected[gid*get_global_size(1)+lid];
    CD&=~(1<<0);
    colDetected[gid*get_global_size(1)+lid]=CD;
    //colDetected[gid*get_global_size(1)+lid]=0;

    float a = sidesData[gid*9];
    float b = sidesData[gid*9+1];
    float c = sidesData[gid*9+2];
    float d = sidesData[gid*9+3];
    float Nv = sidesData[gid*9+4];
    float3 normal =  {sidesData[gid*9+5],sidesData[gid*9+6],sidesData[gid*9+7]};

    float3 ballPos = {ball[lid*4+0],ball[lid*4+1],ball[lid*4+2]};
    float ballSize=ball[lid*4+3];

    float t=(d-(a*ballPos.x+b*ballPos.y+c*ballPos.z))/Nv;
    float3 i = {ballPos.x +(normal.x*t),ballPos.y +(normal.y*t),ballPos.z +(normal.z*t)};
    float distance = ((i.x-ballPos.x)*(i.x-ballPos.x))+((i.y-ballPos.y)*(i.y-ballPos.y))+((i.z-ballPos.z)*(i.z-ballPos.z));
    if(distance<(ballSize*ballSize))
    {
        float3 p1 = {points[sides[gid*3]*3],points[sides[gid*3]*3+1],points[sides[gid*3]*3+2]};
        float3 p2 = {points[sides[gid*3+1]*3],points[sides[gid*3+1]*3+1],points[sides[gid*3+1]*3+2]};
        float3 p3 = {points[sides[gid*3+2]*3],points[sides[gid*3+2]*3+1],points[sides[gid*3+2]*3+2]};

        float3 v0={p3.x-p1.x,p3.y-p1.y,p3.z-p1.z};
        float3 v1={p2.x-p1.x,p2.y-p1.y,p2.z-p1.z};
        float3 v2={ i.x-p1.x, i.y-p1.y, i.z-p1.z};

        float v0v0 = v0.x*v0.x + v0.y*v0.y + v0.z*v0.z;
        float v0v1 = v0.x*v1.x + v0.y*v1.y + v0.z*v1.z;
        float v0v2 = v0.x*v2.x + v0.y*v2.y + v0.z*v2.z;
        float v1v1 = v1.x*v1.x + v1.y*v1.y + v1.z*v1.z;
        float v1v2 = v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;

        float u = (v1v1*v0v2-v0v1*v1v2)/(v0v0*v1v1-v0v1*v0v1);
        float v = (v0v0*v1v2-v0v1*v0v2)/(v0v0*v1v1-v0v1*v0v1);
        if(u>=0 && v>=0 && u<=1 && v<=1 && (u+v)<=1)
        {
            //colDetected[gid]=colDetected[gid]|(1<<(lid));
            //colDetected[gid*get_global_size(1)+lid]=1;
            CD|=(1<<0);
            colDetected[gid*get_global_size(1)+lid]=CD;
        }
    }
}

__kernel void ballEdge(__global const float *points,__global const int *edges, __global const float *edgesData,__global const float *ball,__global char *colDetected)
{
    int gid = get_global_id(0);
    int lid = get_global_id(1);
    char CD=colDetected[gid*get_global_size(1)+lid];
    CD&=~(1<<1);
    colDetected[gid*get_global_size(1)+lid]=CD;

    float l = edgesData[gid*4];
    float3 unit =  {edgesData[gid*4+1],edgesData[gid*4+2],edgesData[gid*4+3]};
    float3 p1 = {points[edges[gid*2]*3],points[edges[gid*2]*3+1],points[edges[gid*2]*3+2]};
    float3 p2 = {points[edges[gid*2+1]*3],points[edges[gid*2+1]*3+1],points[edges[gid*2+1]*3+2]};

    float3 ballPos = {ball[lid*4+0],ball[lid*4+1],ball[lid*4+2]};
    float ballSize=ball[lid*4+3];

    float t=unit.x*(ballPos.x-p1.x)+unit.y*(ballPos.y-p1.y)+unit.z*(ballPos.z-p1.z);
    if (t > 0 && t < l)
    {
        float3 closest={p1.x+unit.x*t,p1.y+unit.y*t,p1.z+unit.z*t};
        float3 perpendicular = {ballPos.x-closest.x,ballPos.y-closest.y,ballPos.z-closest.z};
        float d = perpendicular.x*perpendicular.x+perpendicular.y*perpendicular.y+perpendicular.z*perpendicular.z;
        if(d<(ballSize*ballSize))
        {
            CD|=(1<<1);
            colDetected[gid*get_global_size(1)+lid]=CD;
        }
    }
}

__kernel void ballPoint(__global const float *points,__global const float *ball,__global char *colDetected)
{
    int gid = get_global_id(0);
    int lid = get_global_id(1);
    char CD=colDetected[gid*get_global_size(1)+lid];
    CD&=~(1<<2);
    colDetected[gid*get_global_size(1)+lid]=CD;

    float3 p1 = {points[gid*3],points[gid*3+1],points[gid*3+2]};

    float3 ballPoint = {ball[lid*4+0]-p1.x,ball[lid*4+1]-p1.y,ball[lid*4+2]-p1.z};
    float ballSize=ball[lid*4+3];

    float d = ballPoint.x*ballPoint.x+ballPoint.y*ballPoint.y+ballPoint.z*ballPoint.z;
    if(d<(ballSize*ballSize))
    {
       CD|=(1<<2);
       colDetected[gid*get_global_size(1)+lid]=CD;
    }
}

__kernel void botOpti(__global const float *points,
__global const int *edges, __global const float *edgesData,
__global const int *sides, __global const float *sidesData,
__global float *ball,__global const int *size)
{
    int gid = get_global_id(0);

    int s0 = size[0];
    int s1 = size[1];
    int s2 = size[2];

    float3 ballPos = {ball[gid*8+0],ball[gid*8+1],ball[gid*8+2]};
    float3 ballVel = {ball[gid*8+3],ball[gid*8+4],ball[gid*8+5]};
    float ballSize=ball[gid*8+6];

    float f=0;

    for(int i=0;i<s0;i++){
        float a = sidesData[i*9];
        float b = sidesData[i*9+1];
        float c = sidesData[i*9+2];
        float d = sidesData[i*9+3];
        float Nv = sidesData[i*9+4];
        float3 normal =  {sidesData[i*9+5],sidesData[i*9+6],sidesData[i*9+7]};
        float friction = sidesData[i*9+8];

        float t=(d-(a*ballPos.x+b*ballPos.y+c*ballPos.z))/Nv;
        float3 intersect = {ballPos.x +(normal.x*t),ballPos.y +(normal.y*t),ballPos.z +(normal.z*t)};
        float distance = ((intersect.x-ballPos.x)*(intersect.x-ballPos.x))+((intersect.y-ballPos.y)*(intersect.y-ballPos.y))+((intersect.z-ballPos.z)*(intersect.z-ballPos.z));
        if(distance<(ballSize*ballSize))
        {
            float3 p1 = {points[sides[i*3]*3],points[sides[i*3]*3+1],points[sides[i*3]*3+2]};
            float3 p2 = {points[sides[i*3+1]*3],points[sides[i*3+1]*3+1],points[sides[i*3+1]*3+2]};
            float3 p3 = {points[sides[i*3+2]*3],points[sides[i*3+2]*3+1],points[sides[i*3+2]*3+2]};

            float3 v0={p3.x-p1.x,p3.y-p1.y,p3.z-p1.z};
            float3 v1={p2.x-p1.x,p2.y-p1.y,p2.z-p1.z};
            float3 v2={ intersect.x-p1.x, intersect.y-p1.y, intersect.z-p1.z};

            float v0v0 = v0.x*v0.x + v0.y*v0.y + v0.z*v0.z;
            float v0v1 = v0.x*v1.x + v0.y*v1.y + v0.z*v1.z;
            float v0v2 = v0.x*v2.x + v0.y*v2.y + v0.z*v2.z;
            float v1v1 = v1.x*v1.x + v1.y*v1.y + v1.z*v1.z;
            float v1v2 = v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;

            float u = (v1v1*v0v2-v0v1*v1v2)/(v0v0*v1v1-v0v1*v0v1);
            float v = (v0v0*v1v2-v0v1*v0v2)/(v0v0*v1v1-v0v1*v0v1);
            if(u>=0 && v>=0 && u<=1 && v<=1 && (u+v)<=1)
            {
                float dir = t > 0 ? -1 : 1;
                ballPos.x=intersect.x+(normal.x*dir*ballSize);
                ballPos.y=intersect.y+(normal.y*dir*ballSize);
                ballPos.z=intersect.z+(normal.z*dir*ballSize);
                float dot = (ballVel.x*normal.x+ballVel.y*normal.y+ballVel.z*normal.z)*1.8f;
                ballVel.x=ballVel.x-(normal.x*dot);
                ballVel.y=ballVel.y-(normal.y*dot);
                ballVel.z=ballVel.z-(normal.z*dot);
                f=friction>f?friction:f;
            }
        }
    }

    for(int i=0;i<s1;i++){
        float l = edgesData[i*4];
        float3 unit =  {edgesData[i*4+1],edgesData[i*4+2],edgesData[i*4+3]};
        float3 p1 = {points[edges[i*2]*3],points[edges[i*2]*3+1],points[edges[i*2]*3+2]};
        float3 p2 = {points[edges[i*2+1]*3],points[edges[i*2+1]*3+1],points[edges[i*2+1]*3+2]};

        float t=unit.x*(ballPos.x-p1.x)+unit.y*(ballPos.y-p1.y)+unit.z*(ballPos.z-p1.z);
        if (t > 0 && t < l)
        {
            float3 closest={p1.x+unit.x*t,p1.y+unit.y*t,p1.z+unit.z*t};
            float3 perpendicular = {ballPos.x-closest.x,ballPos.y-closest.y,ballPos.z-closest.z};
            float d = sqrt(perpendicular.x*perpendicular.x+perpendicular.y*perpendicular.y+perpendicular.z*perpendicular.z);
            if(d<ballSize)
            {
                float3 unit = {perpendicular.x/d,perpendicular.y/d,perpendicular.z/d};
                ballPos.x=closest.x+(unit.x*ballSize);
                ballPos.y=closest.y+(unit.y*ballSize);
                ballPos.z=closest.z+(unit.z*ballSize);
                float dot = (ballVel.x*unit.x+ballVel.y*unit.y+ballVel.z*unit.z)*1.8f;
                ballVel.x=ballVel.x-(unit.x*dot);
                ballVel.y=ballVel.y-(unit.y*dot);
                ballVel.z=ballVel.z-(unit.z*dot);
            }
        }
    }

    for(int i=0;i<s0;i++){
        float3 p1 = {points[i*3],points[i*3+1],points[i*3+2]};

        float3 ballPoint = {ballPos.x-p1.x,ballPos.y-p1.y,ballPos.z-p1.z};
        float d = sqrt(ballPoint.x*ballPoint.x+ballPoint.y*ballPoint.y+ballPoint.z*ballPoint.z);
        if(d<ballSize)
        {
           float3 unit = {ballPoint.x/d,ballPoint.y/d,ballPoint.z/d};
           ballPos.x=p1.x+(unit.x*ballSize);
           ballPos.y=p1.y+(unit.y*ballSize);
           ballPos.z=p1.z+(unit.z*ballSize);
           float dot = (ballVel.x*unit.x+ballVel.y*unit.y+ballVel.z*unit.z)*1.8f;
           ballVel.x=ballVel.x-(unit.x*dot);
           ballVel.y=ballVel.y-(unit.y*dot);
           ballVel.z=ballVel.z-(unit.z*dot);
        }
    }

    ball[gid*8+0]=ballPos.x;
    ball[gid*8+1]=ballPos.y;
    ball[gid*8+2]=ballPos.z;
    ball[gid*8+3]=ballVel.x;
    ball[gid*8+4]=ballVel.y;
    ball[gid*8+5]=ballVel.z;
    ball[gid*8+7]=f;
}