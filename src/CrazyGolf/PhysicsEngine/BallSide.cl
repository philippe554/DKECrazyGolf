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